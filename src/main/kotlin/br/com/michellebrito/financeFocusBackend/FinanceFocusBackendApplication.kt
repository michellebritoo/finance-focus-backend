package br.com.michellebrito.financeFocusBackend

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class FinanceFocusBackendApplication {
    @Bean
    fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance(FirebaseApp.getInstance())
    }

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance(FirebaseApp.getInstance())
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
