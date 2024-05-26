package br.com.michellebrito.financeFocusBackend

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinanceFocusBackendApplication

fun main(args: Array<String>) {
    val serviceAccount = FinanceFocusBackendApplication::class.java.getResourceAsStream("/serviceAccountKey.json")

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)
    runApplication<FinanceFocusBackendApplication>(*args)
}
