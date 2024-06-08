package br.com.michellebrito.financeFocusBackend.global.interceptor

import br.com.michellebrito.financeFocusBackend.auth.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthorizationInterceptor(private val authService: AuthService) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authorization = request.getHeader("Authorization")
        if (authorization == null || authorization.isBlank()) {
            response.apply {
                status = HttpServletResponse.SC_UNAUTHORIZED
                contentType = MediaType.APPLICATION_JSON_VALUE
                writer.write("{\"message\":\"Token não enviado\"}")
            }
            return false
        }

        if (!authService.validateToken(authorization)) {
            response.apply {
                status = HttpServletResponse.SC_UNAUTHORIZED
                contentType = MediaType.APPLICATION_JSON_VALUE
                writer.write("{\"message\":\"Token inválido\"}")
            }
            return false
        }

        return true
    }
}