package br.com.michellebrito.financeFocusBackend.userinfo.model

data class UserDetailsModel(
    var name: String,
    var email: String,
    val concludedGoals: Int = 0,
    val rateSimulation: Int = 0
)
