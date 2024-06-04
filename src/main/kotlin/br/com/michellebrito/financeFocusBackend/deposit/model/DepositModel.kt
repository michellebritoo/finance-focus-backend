package br.com.michellebrito.financeFocusBackend.deposit.model

import org.jetbrains.annotations.NotNull
import java.util.*

const val DEPOSIT_PREFIX = "deposit:"

data class DepositModel (
    var id: String = DEPOSIT_PREFIX + UUID.randomUUID(),
    @field:NotNull
    var amount: Float,
    @field:NotNull
    var remainingValue: Float,
    @field:NotNull
    var numberOfDeposit: Int,
    @field:NotNull
    var lastDeposit: Int,
    @field:NotNull
    var lastDepositDate: String,
    var expectedDepositList: MutableList<ExpectedDeposit> = mutableListOf()
)