package br.com.michellebrito.financeFocusBackend.deposit.model

import org.jetbrains.annotations.NotNull
import java.util.*

private const val EXPECTED_DEPOSIT_PREFIX = "EXPECTED_DEPOSIT:"

data class ExpectedDeposit (
    var id: String = EXPECTED_DEPOSIT_PREFIX + UUID.randomUUID(),
    var depositId: String,
    var value: Float,
    var completed: Boolean
)