package demo.calendar.service

import ch.qos.logback.core.subst.Token
import demo.calendar.dto.AuthorizeRequest
import demo.calendar.dto.SingUpRequest
import demo.calendar.dto.User
import demo.calendar.dto.UserResponse
import demo.calendar.entity.TokenEntity
import demo.calendar.entity.UserEntity
import demo.calendar.exception.UserAlreadyRegisteredException
import demo.calendar.exception.UserNotFoundException
import demo.calendar.exception.WrongUserException
import demo.calendar.repository.TokenRepository
import demo.calendar.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {
    fun createToken(user: UserEntity) =
        TokenEntity(
            token_value = UUID.randomUUID().toString(),
            user = user
        )

    @Transactional
    fun registerUser(request: SingUpRequest): UserResponse {
        val user = userRepository.findByTg(request.tg)
        if (user != null) throw UserAlreadyRegisteredException("User with this tg is already registered")
        userRepository.save(UserEntity(
            username = request.userName,
            email = request.email,
            phone = request.phone,
            tg = request.tg
        ))
        return UserResponse(userName = request.userName, email = request.email, phone = request.phone, tg = request.tg)
    }
    fun authorizeUser(request: AuthorizeRequest): String{
        val user = userRepository.findByTg(request.tg)
        if (user == null) throw UserNotFoundException("User with this tg not found")
        if (user.username != request.userName) throw WrongUserException("User with such tg has different username")
        val token = createToken(user)
        return token.token_value
    }
}