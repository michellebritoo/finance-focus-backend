package br.com.michellebrito.financeFocusBackend.deposit.service

import br.com.michellebrito.financeFocusBackend.deposit.repository.DepositRepository
import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class DepositService {
    @Autowired
    lateinit var repository: DepositRepository

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
        val (dateInit, dateFinish) = parseDates(init, finish)
        val diff = if (monthFrequency) {
            ChronoUnit.MONTHS.between(dateInit, dateFinish)
        } else {
            ChronoUnit.WEEKS.between(dateInit, dateFinish)
        }
        val baseDepositValue = amount / diff

        return createDepositModel(amount, baseDepositValue, diff.toInt())
    }

    private fun generateGoalDepositsGradualValue(
        monthFrequency: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): DepositModel {
        val (dateInit, dateFinish) = parseDates(init, finish)
        val diff = if (monthFrequency) {
            ChronoUnit.MONTHS.between(dateInit, dateFinish)
        } else {
            ChronoUnit.WEEKS.between(dateInit, dateFinish)
        }
        val depositValue = amount / diff
        val depositIncrement = depositValue / diff
        var remainingAmount = amount

        val deposit = createDepositModel(amount, amount, diff.toInt())
        for (i in 1..diff) {
            var currentDepositValue = depositValue + depositIncrement * (i - 1)
            if (currentDepositValue > remainingAmount) {
                currentDepositValue = remainingAmount
            }
            val currentExpectedDeposit = ExpectedDeposit(depositId = deposit.id, value = currentDepositValue)
            repository.createExpectedDeposit(currentExpectedDeposit)
            deposit.expectedDepositList.add(currentExpectedDeposit)
            remainingAmount -= currentDepositValue
        }
        return deposit
    }

    private fun parseDates(init: String, finish: String): Pair<LocalDate, LocalDate> {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateInit = LocalDate.parse(init, format)
        val dateFinish = LocalDate.parse(finish, format)
        return Pair(dateInit, dateFinish)
    }

    private fun createDepositModel(amount: Float, remainingValue: Float, numberOfDeposit: Int): DepositModel {
        return DepositModel(
            amount = amount,
            remainingValue = remainingValue,
            numberOfDeposit = numberOfDeposit,
            lastDeposit = 0,
            lastDepositDate = ""
        )
    }
}
