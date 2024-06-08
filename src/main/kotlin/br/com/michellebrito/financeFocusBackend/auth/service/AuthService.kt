package br.com.michellebrito.financeFocusBackend.auth.service

import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Service

@Service
class AuthService(private val firebaseAuth: FirebaseAuth) {
    private var token = ""
    fun validateToken(token: String): Boolean {
        this.token = token
        return try {
            firebaseAuth.verifyIdToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserUIDByToken(): String {
        firebaseAuth.verifyIdToken(token).uid
        return ""
    }
}
