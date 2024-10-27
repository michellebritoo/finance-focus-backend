package br.com.michellebrito.financeFocusBackend.deposit.service

import br.com.michellebrito.financeFocusBackend.deposit.model.Deposit
import br.com.michellebrito.financeFocusBackend.deposit.repository.DepositRepository
import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.utils.extension.parseDates
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit

@Service
class DepositService {
    @Autowired
    lateinit var repository: DepositRepository

    fun getDeposits(id: String): List<ExpectedDeposit> {
        return repository.getDeposit(id)
    }

    fun deleteDeposits(goal: CreateGoalRequest) {
        //todo: voltar depois
//        val deposit = Gson().fromJson(getDeposits(goal.depositId), DepositModel::class.java)
//
//        repository.deleteExpectedDepositByDepositId(deposit.id)
//        repository.deleteDeposit(deposit.id)
    }

    fun generateGoalDeposits(
        goalId: String,
        monthFrequency: Boolean,
        gradualProgress: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): String {
        val createdDeposit = if (gradualProgress) {
            generateGoalDepositsGradualValue(goalId, monthFrequency, amount, init, finish)
        } else {
            generateGoalDepositsFixedValue(goalId, monthFrequency, amount, init, finish)
        }
        //repository.createDeposit(createdDeposit)

        return createdDeposit.id
    }

    private fun generateGoalDepositsFixedValue(
        goalId: String,
        monthFrequency: Boolean,
        amount: Float,
        init: String,
        finish: String
    ): DepositModel {
        val diff = getDiffDates(init, finish, monthFrequency)
        val baseDepositValue = amount / diff

        val deposit = createDepositModel(amount, baseDepositValue, diff.toInt())
        for (i in 1..diff) {
            val currentExpectedDeposit = ExpectedDeposit(value = baseDepositValue, completed = false)
            //repository.createExpectedDeposit(currentExpectedDeposit)
            deposit.expectedDepositList.add(currentExpectedDeposit)
        }

        repository.saveDepositsUnderGoal(goalId, deposit.expectedDepositList)
        return deposit
    }

    private fun generateGoalDepositsGradualValue(
        goalId: String,
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
            val currentExpectedDeposit = ExpectedDeposit(value = currentDepositValue, completed = false)
            //repository.createExpectedDeposit(currentExpectedDeposit)

            deposit.expectedDepositList.add(currentExpectedDeposit)
            remainingAmount -= currentDepositValue
        }

        repository.saveDepositsUnderGoal(goalId, deposit.expectedDepositList)
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

    fun saveExpectedDepositsUnderGoal(goalId: String, depositList: MutableList<ExpectedDeposit>) {
        repository.saveDepositsUnderGoal(goalId, depositList)
    }

    fun updateExpectedDeposit(goalId: String, deposit: ExpectedDeposit) {
        repository.updateExpectedDeposit(goalId, deposit)
    }

    fun updateDeposit(deposit: DepositModel) {
        repository.updateDeposit(deposit)
    }

    fun updateDepositsUnderGoal(id: String, depositList: List<ExpectedDeposit>) {
        repository.updateDepositsUnderGoal(id, depositList)
    }
}
