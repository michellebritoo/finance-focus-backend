package br.com.michellebrito.financeFocusBackend.userinfo.repository

import br.com.michellebrito.financeFocusBackend.auth.service.AuthService
import br.com.michellebrito.financeFocusBackend.userinfo.model.EditUserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import com.google.cloud.firestore.Firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserInfoRepository(private val firebaseAuth: FirebaseAuth) {
    private val firestore: Firestore = FirestoreClient.getFirestore()
    @Autowired
    private lateinit var authService: AuthService

    fun registerNewUser(userUID: String) {
        val firebaseAuth = firebaseAuth.getUser(userUID)
        val userDocument = firestore.collection(USERS_COLLECTION).document(userUID)

        val userData = mapOf(
            "email" to firebaseAuth.email,
            "name" to firebaseAuth.email.split("@").first(),
            "rateSimulation" to 0,
            "concludedGoals" to 0,
            "deviceToken" to ""
        )

        userDocument.set(userData)
    }

    fun getUserDetails(userUID: String): UserDetailsModel {
        val userData = firestore.collection(USERS_COLLECTION).document(userUID).get().get()
        val json = Gson().toJson(userData.data)
        val userDetails = Gson().fromJson(json, UserDetailsModel::class.java)

        return userDetails
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
            detailsModel.toMap()
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

    fun incrementUserGoals() {
        val userUID = authService.getUserUIDByToken()
        val userData = firestore.collection(USERS_COLLECTION).document(userUID)
        val goals = getUserDetails(userUID).concludedGoals
        userData.update(
            mapOf(
                "concludedGoals" to goals.inc()
            )
        )
    }

    fun incrementUserRateSimulation() {
        val userUID = authService.getUserUIDByToken()
        val userData = firestore.collection(USERS_COLLECTION).document(userUID)
        val simulations = getUserDetails(userUID).rateSimulation
        userData.update(
            mapOf(
                "rateSimulation" to simulations.inc()
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

    private fun EditUserDetailsModel.toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email
        ).filterValues { it != null }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
