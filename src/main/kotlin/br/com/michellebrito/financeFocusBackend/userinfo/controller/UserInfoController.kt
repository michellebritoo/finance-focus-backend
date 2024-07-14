package br.com.michellebrito.financeFocusBackend.userinfo.controller

import br.com.michellebrito.financeFocusBackend.userinfo.controller.UserInfoController.Companion.USER
import br.com.michellebrito.financeFocusBackend.userinfo.model.EditUserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.service.UserInfoService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PostMapping(UPDATE)
    fun updateUserDetails(@Valid @RequestBody editUserDetailsModel: EditUserDetailsModel) {
        service.updateUserDetails(editUserDetailsModel)
    }

    @PostMapping(CREATE)
    fun createUser() {
         service.registerNewUser()
    }

    companion object {
        const val USER = "/user"
        const val DETAILS = "/details"
        const val UPDATE = "/update"
        const val CREATE = "/create"
    }
}
