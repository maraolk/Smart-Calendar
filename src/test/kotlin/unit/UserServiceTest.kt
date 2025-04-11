package unit

import demo.calendar.dto.SingUpRequest
import demo.calendar.dto.UserResponse
import demo.calendar.entity.UserEntity
import demo.calendar.exception.UserAlreadyRegisteredException
import demo.calendar.repository.TokenRepository
import demo.calendar.repository.UserRepository
import demo.calendar.service.UserService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows


class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val tokenRepository = mockk<TokenRepository>()
    private val userService = UserService(userRepository, tokenRepository)

    @Test
    fun `Регистрация пользователя, пользователь уже зарегистрирован`() {
        val newUser = SingUpRequest(
            userName = "Адольф",
            phone = "1488",
            email = "AustrianPainter@nazi.de",
            tg = "@amTheRealHitler1337",
            password = "BombardiroCrocodillo",
        )
        every { userRepository.findByTg(newUser.tg) } returns UserEntity(
            username = newUser.userName,
            phone = newUser.phone,
            email = newUser.email,
            tg = newUser.tg,
            password = newUser.password,
        )

        val exception = assertThrows(UserAlreadyRegisteredException::class.java) {
            userService.registerUser(newUser)
        }

        exception.message shouldBe "User with this tg is already registered"

    }
    @Test
    fun `Регистрация пользователя, когда пользователь правильно регистрируется`(){
        val newRequest = SingUpRequest(
            userName = "Ostin",
            phone = "891234567829",
            email = "PUPUPUpupunia@popatarakana",
            tg = "@SIGMABOY",
            password = "BOMBARDIROCROCODILLO",)
        every { userRepository.findByTg(newRequest.tg) } returns null
        every {userRepository.save(UserEntity(
            username=newRequest.userName, phone=newRequest.phone, email=newRequest.email, tg=newRequest.tg, password = newRequest.password))} answers{firstArg()}
        val response = userService.registerUser(newRequest)
        response shouldBe UserResponse(userName=newRequest.userName, phone=newRequest.phone, email=newRequest.email, tg=newRequest.tg, password=newRequest.password)
    }
}