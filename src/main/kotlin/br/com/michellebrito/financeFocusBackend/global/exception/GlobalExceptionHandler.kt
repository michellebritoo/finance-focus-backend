package br.com.michellebrito.financeFocusBackend.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleValidationExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }
}