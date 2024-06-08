package br.com.michellebrito.financeFocusBackend

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class FinanceFocusBackendApplication {
    @Bean
    fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance(FirebaseApp.getInstance())
    }
}

fun main(args: Array<String>) {
    val serviceAccount = FinanceFocusBackendApplication::class.java.getResourceAsStream("/serviceAccountKey.json")

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)
    runApplication<FinanceFocusBackendApplication>(*args)
}
