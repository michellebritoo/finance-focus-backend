@file:Suppress("ThrowableNotThrown")

package br.com.michellebrito.financeFocusBackend.goals.service

import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.service.DepositService
import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.IncrementGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.UpdateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.repository.GoalRepository
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.math.abs

@Service
class GoalsService {
    @Autowired
    lateinit var repository: GoalRepository

    @Autowired
    lateinit var depositService: DepositService

    @Throws(ExecutionException::class, InterruptedException::class)
    fun createGoal(model: CreateGoalRequest) {
        checkInvalidDateInterval(model.initDate, model.finishDate)
        checkGoalValueToCreate(model.totalValue)
        model.depositId = depositService.generateGoalDeposits(
            model.monthFrequency,
            model.gradualProgress,
            model.totalValue,
            model.initDate,
            model.finishDate
        )
        model.remainingValue = model.totalValue
        repository.createGoal(model)
    }

    fun getGoal(id: String): String {
        return repository.getGoal(id) ?: throw IllegalArgumentException("Objetivo não encontrado")
    }

    fun getGoalsByUser(userId: String): String? {
        return repository.getGoalsByUser(userId)
    }

    fun updateGoal(model: UpdateGoalRequest) {
        val existingGoal = Gson().fromJson(getGoal(model.id), CreateGoalRequest::class.java)
        val existingDeposits =
            Gson().fromJson(depositService.getDeposits(existingGoal.depositId), DepositModel::class.java)

        val shouldReWrite = listOf(
            model.totalValue to existingGoal.totalValue,
            model.initDate to existingGoal.initDate,
            model.finishDate to existingGoal.finishDate,
            model.gradualProgress to existingGoal.gradualProgress,
            model.monthFrequency to existingGoal.monthFrequency
        ).any { (newValue, oldValue) -> newValue?.let { it != oldValue } == true }

        if (shouldReWrite) {
            if (existingDeposits.lastDeposit < 0) {
                deleteGoal(existingGoal.id)
                createGoal(
                    CreateGoalRequest(
                        id = existingGoal.id,
                        userUID = existingGoal.userUID,
                        name = model.name ?: existingGoal.name,
                        description = model.description ?: existingGoal.description,
                        totalValue = model.totalValue ?: existingGoal.totalValue,
                        remainingValue = model.totalValue ?: existingGoal.totalValue,
                        gradualProgress = model.gradualProgress ?: existingGoal.gradualProgress,
                        monthFrequency = model.monthFrequency ?: existingGoal.monthFrequency,
                        initDate = model.initDate ?: existingGoal.initDate,
                        finishDate = model.finishDate ?: existingGoal.finishDate
                    )
                )
            } else {
                //task 41
            }
        } else {
            repository.updateGoal(model)
        }
    }

    fun incrementGoal(model: IncrementGoalRequest) {
        val goal = Gson().fromJson(getGoal(model.id), CreateGoalRequest::class.java)
        checkConcludedGoal(goal.concluded)
        checkGoalValueToIncrement(goal.remainingValue, model.valueToIncrement)

        goal.remainingValue -= model.valueToIncrement
        if (goal.remainingValue == 0f) {
            goal.concluded = true
        }

        repository.incrementGoal(model.id, goal.remainingValue, goal.concluded)
    }

    fun deleteGoal(id: String) {
        val goal = Gson().fromJson(repository.getGoal(id), CreateGoalRequest::class.java)
        depositService.deleteDeposits(goal)
        repository.deleteGoal(id)
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

    private fun checkGoalValueToIncrement(remaningValue: Float, value: Float) {
        if (value <= 0f) {
            throw IllegalArgumentException("Não é possível incrementar o valor zero")
        }
        if (remaningValue < value) {
            throw IllegalArgumentException("Não é possível incrementar um valor maior que o valor restante para concluir o objetivo")
        }
    }

    private fun checkConcludedGoal(isConcluded: Boolean) {
        if (isConcluded) {
            throw IllegalArgumentException("Esse objetivo já foi concluído")
        }
    }

    private companion object {
        const val MIN_DAYS = 7
    }
}
