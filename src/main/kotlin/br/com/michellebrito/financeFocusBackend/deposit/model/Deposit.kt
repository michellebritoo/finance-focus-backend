package br.com.michellebrito.financeFocusBackend.deposit.model

import java.util.*

data class Deposit(
    var id: String = "DEPOSIT:" + UUID.randomUUID(),
    var value: Float,
    var completed: Boolean
)
