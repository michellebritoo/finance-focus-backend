package br.com.michellebrito.financeFocusBackend.deposit.service

import br.com.michellebrito.financeFocusBackend.deposit.repository.DepositRepository
import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.utils.extension.parseDates
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit

@Service
class DepositService {
    @Autowired
    lateinit var repository: DepositRepository

    fun getDeposits(id: String): String? {
        return repository.getDeposit(id)
    }

    fun deleteDeposits(goal: CreateGoalRequest) {
        val deposit = Gson().fromJson(getDeposits(goal.depositId), DepositModel::class.java)

        repository.deleteExpectedDepositByDepositId(deposit.id)
        repository.deleteDeposit(deposit.id)
    }

    fun generateGoalDeposits(
        monthFrequency: Boolean,
        gradualProgress: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): String {
        val createdDeposit = if (gradualProgress) {
            generateGoalDepositsGradualValue(monthFrequency, amount, init, finish)
        } else {
            generateGoalDepositsFixedValue(monthFrequency, amount, init, finish)
        }
        repository.createDeposit(createdDeposit)
        return createdDeposit.id
    }

    private fun generateGoalDepositsFixedValue(
        monthFrequency: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): DepositModel {
        val diff = getDiffDates(init, finish, monthFrequency)
        val baseDepositValue = amount / diff

        return createDepositModel(amount, baseDepositValue, diff.toInt())
    }

    private fun generateGoalDepositsGradualValue(
        monthFrequency: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): DepositModel {
        val diff = getDiffDates(init, finish, monthFrequency)
        val depositValue = amount / diff
        val depositIncrement = depositValue / diff
        var remainingAmount = amount

        val deposit = createDepositModel(amount, amount, diff.toInt())
        for (i in 1..diff) {
            var currentDepositValue = depositValue + depositIncrement * (i - 1)
            if (currentDepositValue > remainingAmount) {
                currentDepositValue = remainingAmount
            }
            val currentExpectedDeposit = ExpectedDeposit(depositId = deposit.id, value = currentDepositValue, completed = false)
            repository.createExpectedDeposit(currentExpectedDeposit)
            deposit.expectedDepositList.add(currentExpectedDeposit)
            remainingAmount -= currentDepositValue
        }
        return deposit
    }

    private fun getDiffDates(init: String, finish: String, monthFrequency: Boolean): Long {
        val dates = Pair(init, finish).parseDates()
        return if (monthFrequency) {
            ChronoUnit.MONTHS.between(dates.first, dates.second)
        } else {
            ChronoUnit.WEEKS.between(dates.first, dates.second)
        }
    }

    private fun createDepositModel(amount: Float, remainingValue: Float, numberOfDeposit: Int): DepositModel {
        return DepositModel(
            amount = amount,
            remainingValue = remainingValue,
            numberOfDeposit = numberOfDeposit,
            lastDeposit = -1,
            lastDepositDate = ""
        )
    }
}
