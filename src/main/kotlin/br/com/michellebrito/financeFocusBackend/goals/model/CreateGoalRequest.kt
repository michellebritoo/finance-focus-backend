package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.util.*

private const val GOAL_PREXIF = "GOAL:"

data class CreateGoalRequest(
    var id: String = GOAL_PREXIF + UUID.randomUUID().toString(),
    var userUID: String? = null,
    var depositId: String = "",
    @field:NotBlank(message = "Informe um nome")
    var name: String,
    var description: String? = null,
    @field:NotNull
    var totalValue: Float,
    var remainingValue: Float,
    var gradualProgress: Boolean = false,
    var monthFrequency: Boolean = true,
    @field:NotBlank(message = "Informe uma data de início")
    var initDate: String,
    @field:NotBlank(message = "Informe uma data de conclusão")
    var finishDate: String,
    var concluded: Boolean = false
)
