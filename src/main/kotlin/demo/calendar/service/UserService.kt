package demo.calendar.service

import demo.calendar.dto.SingUpRequest
import demo.calendar.dto.User
import demo.calendar.dto.UserResponse
import demo.calendar.entity.UserEntity
import demo.calendar.exception.UserAlreadyRegisteredException
import demo.calendar.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class UserService(
    private val userRepository: UserRepository
) {
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
}