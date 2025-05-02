package demo.calendar.unit

import demo.calendar.dto.AuthorizeRequest
import demo.calendar.dto.ManageRequest
import demo.calendar.dto.SingUpRequest
import demo.calendar.dto.UserResponse
import demo.calendar.entity.TokenEntity
import demo.calendar.entity.UserEntity
import demo.calendar.exception.*
import demo.calendar.repository.TokenRepository
import demo.calendar.repository.UserRepository
import demo.calendar.service.UserService
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
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
    fun `Проверка валидности токена, токена не существует`() {
        val token: TokenEntity? = null
        val exception = assertThrows(NotValidTokenException::class.java) {
            userService.tokenIsValid(token)
        }
        exception.message shouldBe "No user exists with such token"
    }

    @Test
    fun `Проверка валидности токена, срок действия токена истек`() {
        val token = TokenEntity(
            token = "1",
            user = UserEntity(
                username = "Адольф",
                phone = "1488",
                email = "AustrianPainter@nazi.de",
                tg = "@amTheRealHitler1337",
                password = "BombardiroCrocodillo",
            ),
            revoked = true

        )
        val exception = assertThrows(NotValidTokenException::class.java) {
            userService.tokenIsValid(token)
        }
        exception.message shouldBe "User's token has been already revoked"
    }

    @Test
    fun `Проверка валидности токена, токен валиден`() {
        val token = TokenEntity(
            token = "1",
            user = UserEntity(
                username = "Адольф",
                phone = "1488",
                email = "AustrianPainter@nazi.de",
                tg = "@amTheRealHitler1337",
                password = "BombardiroCrocodillo",
            ),

        )
        shouldNotThrow<NotValidTokenException> {
            userService.tokenIsValid(token)
        }
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
    fun `Регистрация пользователя, пользователь правильно регистрируется`() {
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
    fun `Авторизация пользователя, пользователь пытается зайти с неверным тг`(){
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
    fun `Авторизация пользователя, аккаунт диактивирован`(){
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA",)
        every { userRepository.findByTg(newRequest.tg) } returns UserEntity(
            username = newRequest.userName,
            email = "a",
            phone = "a",
            tg = newRequest.tg,
            password = newRequest.password,
            active = false
        )
        val exception = assertThrows(UserIsDeactivated::class.java) {
            userService.authorizeUser(newRequest)
        }
        exception.message shouldBe "User with this tg is deactivated"
    }

    @Test
    fun `Авторизация пользователя, пользователь пытается зайти в тг с неправильным никнеймом`(){
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA",)
        every { userRepository.findByTg(newRequest.tg) } returns UserEntity(username="Pop", email="jdfjgd@mail.ru",
            phone="789573542874", tg="@SIGMABOY", password="TRALALELOTRALALA")
        val exception = assertThrows(WrongUserException::class.java) {
            userService.authorizeUser(newRequest)
        }
        exception.message shouldBe "User with such tg has different username"
    }

    @Test
    fun `Авторизация пользователя, пользователь пытается зайти в тг с неправильным паролем`(){
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
    fun `Авторизация пользователя, пользователь правильно авторизуется`() {
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
        every { tokenRepository.save(TokenEntity(token = "abc", user=user)) } answers{firstArg()}
        userService.authorizeUser(newRequest) shouldBe "abc"
    }
}
