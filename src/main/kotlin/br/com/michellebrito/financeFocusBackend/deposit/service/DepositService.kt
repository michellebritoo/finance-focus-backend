package br.com.michellebrito.financeFocusBackend.deposit.service

import br.com.michellebrito.financeFocusBackend.deposit.repository.DepositRepository
import br.com.michellebrito.financeFocusBackend.goals.model.DepositModel
import br.com.michellebrito.financeFocusBackend.goals.model.ExpectedDeposit
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
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateInit = LocalDate.parse(init, format)
        val dateFinish = LocalDate.parse(finish, format)

        if (monthFrequency) {
            val diffMonths = ChronoUnit.MONTHS.between(dateInit, dateFinish)
            val depositValue = amount / diffMonths
            return DepositModel(
                amount = depositValue,
                remainingValue = amount,
                numberOfDeposit = diffMonths.toInt(),
                lastDeposit = 0,
                lastDepositDate = ""
            )
        } else {
            val diffWeeks = ChronoUnit.WEEKS.between(dateInit, dateFinish)
            val depositValue = amount / diffWeeks
            return DepositModel(
                amount = depositValue,
                remainingValue = amount,
                numberOfDeposit = diffWeeks.toInt(),
                lastDeposit = 0,
                lastDepositDate = ""
            )
        }
    }

    private fun generateGoalDepositsGradualValue(
        monthFrequency: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): DepositModel {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateInit = LocalDate.parse(init, format)
        val dateFinish = LocalDate.parse(finish, format)

        if (monthFrequency) {
            val diffMonths = ChronoUnit.MONTHS.between(dateInit, dateFinish)
            val depositValue = amount / diffMonths
            val deposits = mutableListOf<Float>()
            val expectedDepositList = mutableListOf<ExpectedDeposit>()
            var currentDepositValue = depositValue
            var currentExpectedDeposit: ExpectedDeposit?

            val depositIncrement = depositValue / diffMonths
            var remainingAmount = amount

            val deposit = DepositModel(
                amount = amount,
                remainingValue = amount,
                numberOfDeposit = diffMonths.toInt(),
                lastDeposit = 0,
                lastDepositDate = ""
            )
            for (i in 1..diffMonths) {
                if (i == diffMonths) {
                    currentDepositValue = remainingAmount
                }
                deposits.add(currentDepositValue)

                currentExpectedDeposit = ExpectedDeposit(depositId = deposit.id, value = currentDepositValue)
                repository.createExpectedDeposit(currentExpectedDeposit)
                expectedDepositList.add(currentExpectedDeposit)

                remainingAmount -= currentDepositValue
                currentDepositValue += depositIncrement
            }
            deposit.expectedDepositList = expectedDepositList
            return deposit
        } else {
            val diffWeeks = ChronoUnit.WEEKS.between(dateInit, dateFinish)
            val depositValue = amount / diffWeeks
            val deposits = mutableListOf<Float>()
            val expectedDepositList = mutableListOf<ExpectedDeposit>()
            var currentDepositValue = depositValue
            var currentExpectedDeposit: ExpectedDeposit?

            val depositIncrement = depositValue / diffWeeks
            var remainingAmount = amount

            val deposit = DepositModel(
                amount = amount,
                remainingValue = amount,
                numberOfDeposit = diffWeeks.toInt(),
                lastDeposit = 0,
                lastDepositDate = ""
            )
            for (i in 1..diffWeeks) {
                if (i == diffWeeks) {
                    currentDepositValue = remainingAmount
                }
                deposits.add(currentDepositValue)
                currentExpectedDeposit = ExpectedDeposit(depositId = deposit.id, value = currentDepositValue)
                expectedDepositList.add(currentExpectedDeposit)
                remainingAmount -= currentDepositValue
                currentDepositValue += depositIncrement
            }
            deposit.expectedDepositList = expectedDepositList
            return deposit
        }
    }
}
