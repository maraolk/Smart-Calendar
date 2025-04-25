package demo.calendar.service

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
import io.kotest.matchers.shouldBe
import demo.calendar.dto.UserResponse
import jakarta.transaction.Transactional

@SpringBootTest(
    properties = [
        "spring.profiles.active=test"
    ]
)
@Transactional
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
            password = "BombardiroCrocodillo",
        )
        userController.registerUser(newUser)
        shouldThrow<UserAlreadyRegisteredException> {
            userController.registerUser(newUser)
        }
    }
    @Test
    fun `Регистрация пользователя, когда пользователь правильно регистрируется`(){
        val newRequest = SingUpRequest(
            userName = "Ostin",
            phone = "891234567829",
            email = "PUPUPUpupunia@popatarakana",
            tg = "@SIGMABOY",
            password = "BOMBARDIROCROCODILLO",)
        val response = userController.registerUser(newRequest)
        response shouldBe UserResponse(userName=newRequest.userName, phone=newRequest.phone, email=newRequest.email, tg=newRequest.tg, password=newRequest.password)
    }
}