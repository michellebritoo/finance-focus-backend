package br.com.michellebrito.financeFocusBackend.deposit.model

import org.jetbrains.annotations.NotNull
import java.util.*

private const val DEPOSIT_PREFIX = "DEPOSIT:"

data class DepositModel (
    var id: String = DEPOSIT_PREFIX + UUID.randomUUID(),
    var amount: Float,
    var remainingValue: Float,
    var numberOfDeposit: Int,
    var lastDeposit: Int,
    var lastDepositDate: String,
    var expectedDepositList: MutableList<ExpectedDeposit> = mutableListOf()
)