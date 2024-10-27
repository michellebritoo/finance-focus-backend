package br.com.michellebrito.financeFocusBackend.deposit.model

import java.util.UUID

private const val EXPECTED_DEPOSIT_PREFIX = "EXPECTED_DEPOSIT:"

data class ExpectedDeposit (
    var id: String = EXPECTED_DEPOSIT_PREFIX + UUID.randomUUID(),
    var value: Float,
    var completed: Boolean
)