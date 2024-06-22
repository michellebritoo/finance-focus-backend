package br.com.michellebrito.financeFocusBackend.userinfo.model

data class UserDetailsModel(
    val name: String,
    val email: String,
    val completedGoals: Int
)
