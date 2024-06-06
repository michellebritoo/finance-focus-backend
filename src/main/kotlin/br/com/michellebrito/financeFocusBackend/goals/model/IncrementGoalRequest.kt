package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class IncrementGoalRequest(
    @field:NotBlank(message = "Informe um id")
    var id: String,
    @field:NotBlank(message = "Realize login para completar a solicitacao")
    var userUID: String,
    @field:NotNull("Informe um valor")
    var valueToIncrement: Float
)
