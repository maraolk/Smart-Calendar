package service

import demo.calendar.controller.UserController
import demo.calendar.dto.SingUpRequest
import demo.calendar.exception.UserAlreadyRegisteredException
import demo.calendar.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Test

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userController: UserController

    @AfterEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun `Регистрация пользователя, пользователь уже зарегистрирован`() {
        val newUser = SingUpRequest(
            userName = "Адольф",
            phone = "1488",
            email = "AustrianPainter@nazi.de",
            tg = "@amTheRealHitler1337",
        )
        userController.registerUser(newUser)
        shouldThrow<UserAlreadyRegisteredException> {
            userController.registerUser(newUser)
        }
    }
}