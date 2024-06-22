package br.com.michellebrito.financeFocusBackend.userinfo.controller

import br.com.michellebrito.financeFocusBackend.userinfo.controller.UserInfoController.Companion.USER
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.service.UserInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(USER)
class UserInfoController {
    @Autowired
    lateinit var service: UserInfoService

    @GetMapping(DETAILS)
    fun getUserDetails(): ResponseEntity<UserDetailsModel> {
        return ResponseEntity.ok(service.getUserDetails())
    }

    companion object {
        const val USER = "/user"
        const val DETAILS = "/details"
    }
}
