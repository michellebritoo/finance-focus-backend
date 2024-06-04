@file:Suppress("ThrowableNotThrown")

package br.com.michellebrito.financeFocusBackend.goals.service

import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.service.DepositService
import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
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
        checkGoalValue(model.totalValue)
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

    fun updateGoal(model: UpdateGoalRequest) {
        val existingGoal = Gson().fromJson(getGoal(model.id), CreateGoalRequest::class.java)
        val existingDeposits = Gson().fromJson(depositService.getDeposits(existingGoal.depositId), DepositModel::class.java)

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

            }
        } else {
            repository.updateGoal(model)
        }
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
        val currentDate = Date()

        val diffDays = abs(dateFinish.time - dateInit.time) / (24 * 60 * 60 * 1000)

        return when {
            diffDays.toInt() < MIN_DAYS -> {
                throw IllegalArgumentException("O objetivo deve durar ao menos um dia")
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

    private fun checkGoalValue(value: Float) {
        if (value <= 1f) {
            throw IllegalArgumentException("O valor do objetivo deve ser maior que R$ 1,00")
        }
    }

    private companion object {
        const val MIN_DAYS = 2
    }
}
