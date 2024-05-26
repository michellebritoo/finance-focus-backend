package br.com.michellebrito.financeFocusBackend.goals.model

data class Goal(
    var name: String = "",
    var description: String = "",
    val value: Float = 0f,
    var gradativeProgress: Boolean = false,
    var monthFrequency: Boolean = false,
    var initDate: String = "",
    var finishDate: String = ""
)