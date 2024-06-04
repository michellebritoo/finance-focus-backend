package br.com.michellebrito.financeFocusBackend.deposit.model

import org.jetbrains.annotations.NotNull
import java.util.*

const val EXPECTED_DEPOSIT_PREFIX = "expectedDeposit:"

data class ExpectedDeposit (
    var id: String = EXPECTED_DEPOSIT_PREFIX + UUID.randomUUID(),
    @field:NotNull
    var depositId: String,
    @field:NotNull
    var value: Float
)