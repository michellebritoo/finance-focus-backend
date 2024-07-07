package br.com.michellebrito.financeFocusBackend.notification.controller

import br.com.michellebrito.financeFocusBackend.notification.service.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@Controller
class NotificationController {
    @Autowired
    lateinit var service: NotificationService

    @PostMapping(REFRESH_DEVICE_TOKEN)
    fun refreshNotificationToken(@RequestHeader deviceToken: String): ResponseEntity<Void> {
        service.refreshDeviceToken(deviceToken)
        return ResponseEntity.ok().build()
    }

    private companion object {
        const val REFRESH_DEVICE_TOKEN = "/refresh/device/token"
    }
}
