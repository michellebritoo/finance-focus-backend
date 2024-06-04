package br.com.michellebrito.financeFocusBackend.goals.model

import jakarta.validation.constraints.NotBlank

data class UpdateGoalRequest(
    @field:NotBlank(message = "Informe um id")
    var id: String,
    @field:NotBlank(message = "Realize login para completar a solicitacao")
    var userUID: String,
    var name: String? = null,
    var description: String? = null,
    var value: Float? = null,
    var gradualProgress: Boolean? = null,
    var monthFrequency: Boolean? = null,
    var initDate: String? = null,
    var finishDate: String? = null
)