package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank

data class UpdateGoalRequest(
    @field:NotBlank(message = "Informe um id")
    var id: String,
    var userUID: String? = null,
    var name: String? = null,
    var description: String? = null,
    var totalValue: Float? = null,
    var remainingValue: Float? = null,
    var gradualProgress: Boolean? = null,
    var monthFrequency: Boolean? = null,
    var initDate: String? = null,
    var finishDate: String? = null
)