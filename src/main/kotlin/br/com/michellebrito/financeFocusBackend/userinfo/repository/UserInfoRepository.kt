package br.com.michellebrito.financeFocusBackend.userinfo.repository

import br.com.michellebrito.financeFocusBackend.goals.service.GoalsService
import br.com.michellebrito.financeFocusBackend.userinfo.model.EditUserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
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

    fun updateUserDetails(detailsModel: EditUserDetailsModel, userUID: String) {
        val updateUser = UserRecord.UpdateRequest(userUID)

        detailsModel.email?.let {
            if (isValidEmail(detailsModel.email)) {
                updateUser.setEmail(detailsModel.email)
            } else {
                throw IllegalArgumentException("E-mail inválido")
            }
        }

        detailsModel.name?.let {
            if (isNameValid(detailsModel.name)) {
                updateUser.setDisplayName(detailsModel.name)
            } else {
                throw IllegalArgumentException("Informe um nome")
            }
        }

        firebaseAuth.updateUser(updateUser)
    }

    private fun isValidEmail(email: String?): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        return emailRegex.matches(email.toString())
    }

    private fun isNameValid(name: String?): Boolean {
        return (name.toString().isEmpty() || name.isNullOrBlank()).not()
    }
}
