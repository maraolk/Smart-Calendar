package unit

import demo.calendar.dto.AuthorizeRequest
import demo.calendar.dto.SingUpRequest
import demo.calendar.dto.UserResponse
import demo.calendar.entity.TokenEntity
import demo.calendar.entity.UserEntity
import demo.calendar.exception.UserAlreadyRegisteredException
import demo.calendar.exception.UserNotFoundException
import demo.calendar.exception.WrongPasswordException
import demo.calendar.repository.TokenRepository
import demo.calendar.repository.UserRepository
import demo.calendar.service.UserService
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*


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
    fun `Регистрация пользователя, когда пользователь правильно регистрируется`() {
        val newRequest = SingUpRequest(
            userName = "Ostin",
            phone = "891234567829",
            email = "PUPUPUpupunia@popatarakana",
            tg = "@SIGMABOY",
            password = "BOMBARDIROCROCODILLO",
        )
        every { userRepository.findByTg(newRequest.tg) } returns null
        every {
            userRepository.save(
                UserEntity(
                    username = newRequest.userName,
                    phone = newRequest.phone,
                    email = newRequest.email,
                    tg = newRequest.tg,
                    password = newRequest.password
                )
            )
        } answers { firstArg() }
        val response = userService.registerUser(newRequest)
        response shouldBe UserResponse(
            userName = newRequest.userName,
            phone = newRequest.phone,
            email = newRequest.email,
            tg = newRequest.tg,
            password = newRequest.password
        )
    }
    @Test
    fun `Авторизация пользователя, когда он пытается зайти с неверным тг`(){
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA",)
        every { userRepository.findByTg(newRequest.tg) } returns null
        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.authorizeUser(newRequest)
        }
        exception.message shouldBe "User with this tg not found"
    }

    @Test
    fun `Авторизация пользователя, когда он пытается зайти в тг с неправильным никнеймом`(){
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA",)
        every { userRepository.findByTg(newRequest.tg) } returns UserEntity(username="Pop", email="jdfjgd@mail.ru",
            phone="789573542874", tg="@SIGMABOY", password="TRALALELOTRALALA")
        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.authorizeUser(newRequest)
        }
        exception.message shouldBe "User with such tg has different username"
    }

    @Test
    fun `Авторизация пользователя, когда он пытается зайти в тг с неправильным паролем`(){
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA",)
        every { userRepository.findByTg(newRequest.tg) } returns UserEntity(username="Bob", email="jdfjgd@mail.ru",
            phone="789573542874", tg="@SIGMABOY", password="pupupu")
        val exception = assertThrows(WrongPasswordException::class.java) {
            userService.authorizeUser(newRequest)
        }
        exception.message shouldBe "User with such tg has different password"
    }

    @Test
    fun `Авторизация пользователя, когда пользователь правильно авторизуется`() {
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA",)
        val user = UserEntity(
            username = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA"
        )
        every { userRepository.findByTg(newRequest.tg) } returns user
        mockkStatic(UUID::class)
        every { UUID.randomUUID().toString() } returns "abc"
        every { tokenRepository.save(TokenEntity(token_value = "abc", user=user)) } answers{firstArg()}
        userService.authorizeUser(newRequest) shouldBe "abc"
    }
}
