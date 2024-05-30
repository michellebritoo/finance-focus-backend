package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class CreateGoalRequest (
    @field:NotBlank(message = "Por favor, informe um nome")
    var name: String,
    var description: String,
    @field:NotNull
    var value: Float,
    var gradualProgress: Boolean = false,
    var monthFrequency: Boolean = true,
    @field:NotBlank(message = "Por favor, informe uma data de início")
    var initDate: String,
    @field:NotBlank(message = "Por favor, informe uma data de conclusão")
    var finishDate: String
)
