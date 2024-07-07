package br.com.michellebrito.financeFocusBackend.userinfo.repository

import br.com.michellebrito.financeFocusBackend.goals.service.GoalsService
import br.com.michellebrito.financeFocusBackend.userinfo.model.EditUserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import com.google.cloud.firestore.Firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserInfoRepository(private val firebaseAuth: FirebaseAuth) {
    @Autowired
    lateinit var goalsService: GoalsService

    private val firestore: Firestore = FirestoreClient.getFirestore()

    fun getUserDetails(userUID: String): UserDetailsModel {
        val userData = firebaseAuth.getUser(userUID)
        val completedGoals = goalsService.countCompleteGoalsByUser()
        return UserDetailsModel(
            userData.displayName ?: userData.email.split("@").first(),
            userData.email,
            completedGoals
        )
    }

    fun getUserDeviceToken(userUID: String): String {
        val userData = firestore.collection(USERS_COLLECTION).document(userUID).get().get()
        val deviceToken = userData.getString("deviceToken") ?: ""
        return deviceToken
    }

    fun getAllUserUIDs(): List<String> {
        val users = firestore.collection(USERS_COLLECTION).get().get().documents
        return users.map { it.id }
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
        firestore.collection(USERS_COLLECTION).document(userUID).update(
            mapOf(
                "email" to detailsModel.email,
                "name" to detailsModel.name
            )
        )
    }

    fun updateUserDeviceToken(userUID: String, token: String) {
        val userData = firestore.collection(USERS_COLLECTION).document(userUID)
        userData.update(
            mapOf(
                "deviceToken" to token
            )
        )
    }

    private fun isValidEmail(email: String?): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        return emailRegex.matches(email.toString())
    }

    private fun isNameValid(name: String?): Boolean {
        return (name.toString().isEmpty() || name.isNullOrBlank()).not()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
