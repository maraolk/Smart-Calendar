package demo.calendar.controlleradvice

import demo.calendar.dto.ErrorResponse
import demo.calendar.exception.*
import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class GeneralControllerAdvice {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(UserAlreadyRegisteredException::class)
    fun alreadyRegisteredExceptionHandler(exception: UserAlreadyRegisteredException) : ResponseEntity<ErrorResponse> {
        logger.warn("User with this tg is already registered", exception)
        return ResponseEntity.status(400).body(ErrorResponse(exception.message ?: "ALREADY REGISTERED"))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun notFoundException(exception: UserNotFoundException) : ResponseEntity<ErrorResponse> {
        logger.warn("User with this tg not found", exception)
        return ResponseEntity.status(404).body(ErrorResponse(exception.message ?: "NOT FOUND"))
    }

    @ExceptionHandler(WrongUserException::class)
    fun wrongUserException(exception: WrongUserException) : ResponseEntity<ErrorResponse> {
        logger.warn("User with such tg has different username", exception)
        return ResponseEntity.status(401).body(ErrorResponse(exception.message ?: "WRONG USER"))
    }

    @ExceptionHandler(WrongPasswordException::class)
    fun wrongPasswordException(exception: WrongPasswordException) : ResponseEntity<ErrorResponse> {
        logger.warn("User with such token has different password", exception)
        return ResponseEntity.status(401).body(ErrorResponse(exception.message ?: "WRONG PASSWORD"))
    }

    @ExceptionHandler(NotValidTokenException::class)
    fun notValidTokenException(exception: NotValidTokenException) : ResponseEntity<ErrorResponse> {
        logger.warn("No user exists with such token", exception)
        return ResponseEntity.status(401).body(ErrorResponse(exception.message ?: "NOT VALID TOKEN"))
    }

}