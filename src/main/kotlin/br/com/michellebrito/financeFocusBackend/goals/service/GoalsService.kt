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
            userUID = getUserUIDByToken()
            remainingValue = model.totalValue
        }
        repository.createGoal(model)

        with(model) {
            depositService.generateGoalDeposits(id, monthFrequency, gradualProgress, totalValue, initDate, finishDate)
        }
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
        val goal = Gson().fromJson(getGoal(model.id), CreateGoalRequest::class.java)

        if (goal.userUID != getUserUIDByToken()) { throw IllegalArgumentException("Objetivo não pertence ao usuário") }

        repository.updateGoal(model)
    }

    fun preIncrement(id: String): List<ExpectedDeposit> {
        return depositService.getDeposits(id)
    }

    fun incrementGoal(model: IncrementGoalRequest) {
        val goal = Gson().fromJson(getGoal(model.goalId), CreateGoalRequest::class.java)
        checkConcludedGoal(goal.concluded)
        checkGoalValueToIncrement(goal.remainingValue, model.valueToIncrement)

        if (goal.userUID != getUserUIDByToken()) { throw IllegalArgumentException("Objetivo não pertence ao usuário") }

        goal.remainingValue -= model.valueToIncrement

        depositService.incrementDeposit(model)

        if (goal.remainingValue <= 0f) {
            goal.remainingValue = 0f
            goal.concluded = true
            userInfoService.incrementUserGoals()
        }

        repository.incrementGoal(model.goalId, goal.remainingValue, goal.concluded)
    }

    fun deleteGoal(id: String) {
        val goal = Gson().fromJson(repository.getGoal(id), CreateGoalRequest::class.java)
        goal?.let {
            if (goal.userUID != getUserUIDByToken()) {
                throw IllegalArgumentException("Objetivo não pertence ao usuário")
            }
            repository.deleteGoal(id)
        }
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
