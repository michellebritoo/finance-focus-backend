package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class IncrementGoalRequest(
    @field:NotBlank(message = "Informe um id")
    var id: String,
    var userUID: String? = null,
    @field:NotNull("Informe um valor")
    var valueToIncrement: Float
)
