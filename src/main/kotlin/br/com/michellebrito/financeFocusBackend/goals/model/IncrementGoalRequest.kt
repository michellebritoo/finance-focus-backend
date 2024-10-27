package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class IncrementGoalRequest(
    @field:NotBlank(message = "Informe id do objetivo")
    var goalId: String,
    @field:NotBlank(message = "Informe id do deposito")
    var expectedDepositId: String,
    @field:NotNull("Informe um valor")
    var valueToIncrement: Float
)
