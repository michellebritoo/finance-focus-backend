package br.com.michellebrito.financeFocusBackend.userinfo.service

import br.com.michellebrito.financeFocusBackend.auth.service.AuthService
import br.com.michellebrito.financeFocusBackend.userinfo.model.EditUserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.model.UserDetailsModel
import br.com.michellebrito.financeFocusBackend.userinfo.repository.UserInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserInfoService(private val authService: AuthService) {
    @Autowired
    lateinit var repository: UserInfoRepository

    fun getUserDetails(): UserDetailsModel {
        val uuid = authService.getUserUIDByToken()
        return repository.getUserDetails(uuid)
    }

    fun updateUserDetails(editUserDetailsModel: EditUserDetailsModel) {
        val uuid = authService.getUserUIDByToken()
        repository.updateUserDetails(editUserDetailsModel, uuid)
    }

    fun incrementUserGoals() {
        repository.incrementUserGoals()
    }

    fun registerNewUser() {
        val uuid = authService.getUserUIDByToken()
        repository.registerNewUser(uuid)
    }
}
