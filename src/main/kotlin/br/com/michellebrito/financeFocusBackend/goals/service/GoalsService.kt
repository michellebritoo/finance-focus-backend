package br.com.michellebrito.financeFocusBackend.goals.service

import br.com.michellebrito.financeFocusBackend.auth.service.AuthService
import br.com.michellebrito.financeFocusBackend.deposit.model.Deposit
import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import br.com.michellebrito.financeFocusBackend.deposit.service.DepositService
import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.IncrementGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.UpdateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.repository.GoalRepository
import br.com.michellebrito.financeFocusBackend.userinfo.service.UserInfoService
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Service
class GoalsService {
    @Autowired
    private lateinit var repository: GoalRepository

    @Autowired
    private lateinit var depositService: DepositService

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var userInfoService: UserInfoService

    fun createGoal(model: CreateGoalRequest) {
        checkInvalidDateInterval(model.initDate, model.finishDate)
        checkGoalValueToCreate(model.totalValue)
        model.apply {
//            depositId = depositService.generateGoalDeposits(
//                model.monthFrequency,
//                model.gradualProgress,
//                model.totalValue,
//                model.initDate,
//                model.finishDate
//            )
            userUID = getUserUIDByToken()
            remainingValue = model.totalValue
        }
        repository.createGoal(model)

        with(model) {
            depositService.generateGoalDeposits(id, monthFrequency, gradualProgress, totalValue, initDate, finishDate)
        }

        //depositService.createDepositsUnderGoal(model.id, depositList)
    }

    private fun createDeposits(goalModel: CreateGoalRequest): List<Deposit> {
        val depositList = mutableListOf<Deposit>()
        val depositValue = goalModel.totalValue / 12

        for (i in 0 until 12) {
            val deposit = Deposit(
                value = depositValue,
                completed = false
            )
            depositList.add(deposit)
        }

        return depositList
    }

    fun getGoal(id: String): String {
        val goal = Gson().fromJson(repository.getGoal(id), CreateGoalRequest::class.java)
            ?: throw IllegalArgumentException("Objetivo não encontrado")
        if (goal.userUID != getUserUIDByToken()) {
            throw IllegalArgumentException("Objetivo não pertence ao usuário")
        }
        return Gson().toJson(goal)
    }

    fun getGoalsByUser(): String? {
        val userID = getUserUIDByToken()
        val goalList = repository.getGoalsByUser(userID)
        val filteredList = goalList?.filter { it.userUID == userID }
        return Gson().toJson(filteredList)
    }

    fun countCompleteGoalsByUser(): Int {
        val userUID = authService.getUserUIDByToken()
        return repository.getGoalsByUser(userUID)?.count { it.concluded } ?: 0
    }

    fun updateGoal(model: UpdateGoalRequest) {
//        val existingGoal = Gson().fromJson(getGoal(model.id), CreateGoalRequest::class.java)
//        val existingDeposits = Gson().fromJson(depositService.getDeposits(existingGoal.depositId), DepositModel::class.java)
//
//        if (existingGoal.userUID != getUserUIDByToken()) {
//            throw IllegalArgumentException("Objetivo não pertence ao usuário")
//        }
//
//        val shouldReWrite = listOf(
//            model.totalValue to existingGoal.totalValue,
//            model.initDate to existingGoal.initDate,
//            model.finishDate to existingGoal.finishDate,
//            model.gradualProgress to existingGoal.gradualProgress,
//            model.monthFrequency to existingGoal.monthFrequency
//        ).any { (newValue, oldValue) -> newValue?.let { it != oldValue } == true }
//
//        if (shouldReWrite) {
//            if (existingDeposits.lastDeposit < 0) {
//                deleteGoal(existingGoal.id)
//                createGoal(
//                    CreateGoalRequest(
//                        id = existingGoal.id,
//                        userUID = existingGoal.userUID,
//                        name = model.name ?: existingGoal.name,
//                        description = model.description ?: existingGoal.description,
//                        totalValue = model.totalValue ?: existingGoal.totalValue,
//                        remainingValue = model.totalValue ?: existingGoal.totalValue,
//                        gradualProgress = model.gradualProgress ?: existingGoal.gradualProgress,
//                        monthFrequency = model.monthFrequency ?: existingGoal.monthFrequency,
//                        initDate = model.initDate ?: existingGoal.initDate,
//                        finishDate = model.finishDate ?: existingGoal.finishDate
//                    )
//                )
//            } else {
//                //task 41
//            }
//        } else {
//            repository.updateGoal(model)
//        }
    }

//    fun preIncrement(id: String): MutableList<ExpectedDeposit> {
////        val goal = Gson().fromJson(getGoal(id), CreateGoalRequest::class.java)
////        val expectedDeposit = Gson().fromJson(depositService.getDeposits(goal.depositId), DepositModel::class.java)
////
////        return  expectedDeposit.expectedDepositList
//    }

    fun incrementGoal(model: IncrementGoalRequest) {
        val goal = Gson().fromJson(getGoal(model.goalId), CreateGoalRequest::class.java)
        checkConcludedGoal(goal.concluded)
        checkGoalValueToIncrement(goal.remainingValue, model.valueToIncrement)

        if (goal.userUID != getUserUIDByToken()) {
            throw IllegalArgumentException("Objetivo não pertence ao usuário")
        }

        goal.remainingValue -= model.valueToIncrement

        val depositList = depositService.getDeposits(model.goalId)

        val depositToComplete = depositList.filter { it.id == model.expectedDepositId }.firstOrNull()
        depositToComplete?.let {
            val remainingDeposits = depositList.filter { !it.completed }
            val isLastDeposit = remainingDeposits.size == 1

            if (isLastDeposit && it.value != model.valueToIncrement) {
                throw IllegalArgumentException("O valor do último depósito deve ser igual ao valor esperado")
            }

            val differenceValue = it.value - model.valueToIncrement

            if (differenceValue > 0f) {
                it.value = model.valueToIncrement
                it.completed = true
                depositService.updateExpectedDeposit(goal.id, it)

                val remainingDeposits = depositList.filter { !it.completed }
                val additionalValuePerDeposit = differenceValue / remainingDeposits.size
                remainingDeposits.forEach { deposit ->
                    deposit.value += additionalValuePerDeposit
                    depositService.updateExpectedDeposit(goal.id, deposit)
                }
            } else {
                it.value = model.valueToIncrement
                it.completed = true
                depositService.updateExpectedDeposit(goal.id, it)
            }
        }

        if (goal.remainingValue <= 0f) {
            goal.remainingValue = 0f
            goal.concluded = true
            userInfoService.incrementUserGoals()
        }

        repository.incrementGoal(model.goalId, goal.remainingValue, goal.concluded)

//        val depositToComplete = deposit.expectedDepositList.firstOrNull { it.value == model.valueToIncrement && !it.completed }
//        depositToComplete?.let {
//            it.completed = true
//            depositService.updateExpectedDeposit(it)
//
//            val index = deposit.expectedDepositList.indexOf(it)
//            if (index != -1) {
//                deposit.expectedDepositList[index] = it
//                depositService.updateDeposit(deposit)
//            }
//        }
//
//        if (goal.remainingValue == 0f) {
//            goal.concluded = true
//            userInfoService.incrementUserGoals()
//        }
//
//        repository.incrementGoal(model.id, goal.remainingValue, goal.concluded)
    }

    fun deleteGoal(id: String) {
//        val goal = Gson().fromJson(repository.getGoal(id), CreateGoalRequest::class.java)
//        goal?.let {
//            if (goal.userUID != getUserUIDByToken()) {
//                throw IllegalArgumentException("Objetivo não pertence ao usuário")
//            }
//            depositService.deleteDeposits(goal)
//            repository.deleteGoal(id)
//        }
    }

    private fun checkInvalidDateInterval(init: String, finish: String): Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val dateInit = format.parse(init)
        val dateFinish = format.parse(finish)
        val currentDate = format.parse(format.format(Date()))

        val diffDays = abs(dateFinish.time - dateInit.time) / (24 * 60 * 60 * 1000)

        return when {
            diffDays.toInt() < MIN_DAYS -> {
                throw IllegalArgumentException("O objetivo deve durar ao menos uma semana")
            }

            dateFinish.before(dateInit) -> {
                throw IllegalArgumentException("O objetivo deve ter uma data de conclusão posterior a data de início")
            }

            dateInit.before(currentDate) -> {
                throw IllegalArgumentException("O objetivo não deve ter uma data de início anterior ao dia de hoje")
            }

            dateFinish.before(currentDate) -> {
                throw IllegalArgumentException("O objetivo deve ter uma data de conclusão posterior ao dia de hoje")
            }

            else -> true
        }
    }

    private fun checkGoalValueToCreate(value: Float) {
        if (value <= 1f) {
            throw IllegalArgumentException("O valor do objetivo deve ser maior que R$ 1,00")
        }
    }

    private fun checkGoalValueToIncrement(remainingValue: Float, value: Float) {
        if (value <= 0f) {
            throw IllegalArgumentException("Não é possível incrementar o valor zero")
        }
        if (remainingValue < value) {
            throw IllegalArgumentException("Não é possível incrementar um valor maior que o valor restante para concluir o objetivo")
        }
    }

    private fun checkConcludedGoal(isConcluded: Boolean) {
        if (isConcluded) {
            throw IllegalArgumentException("Esse objetivo já foi concluído")
        }
    }

    private fun getUserUIDByToken() = authService.getUserUIDByToken()

    private companion object {
        const val MIN_DAYS = 7
    }
}
