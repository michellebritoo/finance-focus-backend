@file:Suppress("ThrowableNotThrown")

package br.com.michellebrito.financeFocusBackend.goals.service

import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.UpdateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.repository.GoalRepository
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

    @Throws(ExecutionException::class, InterruptedException::class)
    fun createGoal(goalModel: CreateGoalRequest) {
        checkInvalidDateInterval(goalModel.initDate, goalModel.finishDate)
        checkGoalValue(goalModel.value)

        repository.createGoal(goalModel)
    }

    fun getGoal(id: String): UpdateGoalRequest? {
        return repository.getGoal(id)
    }

    fun updateGoal(goalModel: UpdateGoalRequest) {
        if (goalModel.initDate != null && goalModel.finishDate != null) {
            checkInvalidDateInterval(goalModel.initDate!!, goalModel.finishDate!!)
        }
        goalModel.value?.let { checkGoalValue(it) }

        repository.updateGoal(goalModel)
    }

    fun deleteGoal(id: String): String {
        return repository.deleteGoal(id)
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
