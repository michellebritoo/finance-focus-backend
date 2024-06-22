package br.com.michellebrito.financeFocusBackend.userinfo.repository

import br.com.michellebrito.financeFocusBackend.goals.service.GoalsService
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserInfoRepository(private val firebaseAuth: FirebaseAuth) {
    @Autowired
    lateinit var goalsService: GoalsService

    fun getUserDetails(userUID: String): UserDetailsModel {
        val userData = firebaseAuth.getUser(userUID)
        val completedGoals = goalsService.countCompleteGoalsByUser()
        return UserDetailsModel(
            userData.displayName ?: userData.email.split("@").first(),
            userData.email,
            completedGoals
        )
    }
}