package br.com.michellebrito.financeFocusBackend.notification.service

import br.com.michellebrito.financeFocusBackend.auth.service.AuthService
import br.com.michellebrito.financeFocusBackend.userinfo.repository.UserInfoRepository
import br.com.michellebrito.financeFocusBackend.utils.extension.isBlankOrEmpty
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class NotificationService {
    @Autowired
    private lateinit var firebaseMessaging: FirebaseMessaging

    @Autowired
    private lateinit var userInfoRepository: UserInfoRepository

    @Autowired
    private lateinit var authService: AuthService

    @Scheduled(cron = "0 00 14 * * ?")
    fun sendDailyNotification() {
        userInfoRepository.getAllUserUIDs().forEach {
            val deviceToken = userInfoRepository.getUserDeviceToken(it)
            if (deviceToken.isBlankOrEmpty().not()) {
                sendNotification(deviceToken, DEFAULT_TITLE, DEFAULT_MESSAGE)
            }
        }
    }

    fun sendNotification(deviceToken: String, title: String, message: String) {
        val message = Message.builder()
            .setToken(deviceToken)
            .putData("title", title)
            .putData("message", message)
            .build()

        firebaseMessaging.send(message)
    }

    fun refreshDeviceToken(token: String) {
        val userUID = authService.getUserUIDByToken()
        userInfoRepository.updateUserDeviceToken(userUID, token)
    }

    private companion object {
        const val DEFAULT_TITLE = "Opa! O apoio chegou \uD83D\uDCAA\uD83D\uDE80"
        const val DEFAULT_MESSAGE = "Acesse o app agora mesmo e continue monitorando seus objetivos"
    }
}