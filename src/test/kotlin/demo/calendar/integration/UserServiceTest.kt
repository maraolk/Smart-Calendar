package demo.calendar.integration

import demo.calendar.dto.AuthorizeRequest
import demo.calendar.dto.ManageRequest
import demo.calendar.dto.SingUpRequest
import demo.calendar.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import demo.calendar.dto.UserResponse
import demo.calendar.entity.TokenEntity
import demo.calendar.entity.UserEntity
import demo.calendar.exception.*
import demo.calendar.repository.TokenRepository
import demo.calendar.service.UserService
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var tokenRepository: TokenRepository

    @Autowired
    lateinit var userService: UserService


    @AfterEach
    fun cleanup() {
        tokenRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `Проверка валидности токена, токена не существует`() {
        val token: TokenEntity? = null
        shouldThrow<NotValidTokenException> {
            userService.tokenIsValid(token)
        }
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
        shouldThrow<NotValidTokenException> {
            userService.tokenIsValid(token)
        }
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
        userRepository.save(UserEntity(
            username = newUser.userName,
            phone = newUser.phone,
            email = newUser.email,
            tg = newUser.tg,
            password = newUser.password,
        ))
        shouldThrow<UserAlreadyRegisteredException> {
            userService.registerUser(newUser)
        }
    }
    @Test
    fun `Регистрация пользователя, пользователь правильно регистрируется`(){
        val newRequest = SingUpRequest(
            userName = "Ostin",
            phone = "891234567829",
            email = "PUPUPUpupunia@popatarakana",
            tg = "@SIGMABOY",
            password = "BOMBARDIROCROCODILLO",)
        val response = userService.registerUser(newRequest)
        response shouldBe UserResponse(userName=newRequest.userName, phone=newRequest.phone, email=newRequest.email, tg=newRequest.tg, password=newRequest.password)
    }

    @Test
    fun `Авторизация пользователя, пользователь пытается зайти с неверным тг`() {
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA"
        )
        shouldThrow<UserNotFoundException> {
            userService.authorizeUser(newRequest)
        }
    }

    @Test
    fun `Авторизация пользователя, аккаунт деактивирован`() {
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA"
        )
        userRepository.save(UserEntity(
            username = newRequest.userName,
            tg = newRequest.tg,
            password = newRequest.password,
            phone = "789672536827",
            email = "@BOMBOMBINIGUSINI",
            active = false
        ))
        shouldThrow<UserIsDeactivated> {
            userService.authorizeUser(newRequest)
        }
    }

    @Test
    fun `Авторизация пользователя, пользователь вводит неправильный никнейм`() {
        val newRequest = AuthorizeRequest(
            userName = "Bob1",
            tg = "@SIGMABOY",
            password = "TRALALELOTRALALA"
        )
        userRepository.save(UserEntity(
            username = "Bob",
            tg = newRequest.tg,
            password = newRequest.password,
            phone = "789672536827",
            email = "@BOMBOMBINIGUSINI",
        ))
        shouldThrow<WrongUserException> {
            userService.authorizeUser(newRequest)
        }
    }

    @Test
    fun `Авторизация пользователя, пользователь вводит неправильный пароль`() {
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "BRBRPATAPIM"
        )
        userRepository.save(UserEntity(
            username = newRequest.userName,
            tg = newRequest.tg,
            password = "TRALALELOTRALALA",
            phone = "789672536827",
            email = "@BOMBOMBINIGUSINI",
        ))
        shouldThrow<WrongPasswordException> {
            userService.authorizeUser(newRequest)
        }
    }

    @Test
    fun `Авторизация пользователя, пользователь успешно авторизируется`() {
        val newRequest = AuthorizeRequest(
            userName = "Bob",
            tg = "@SIGMABOY",
            password = "BRBRPATAPIM"
        )
        val user = UserEntity(
            username = newRequest.userName,
            tg = newRequest.tg,
            password = newRequest.password,
            phone = "789672536827",
            email = "@BOMBOMBINIGUSINI",
        )
        userRepository.save(user)
        mockkStatic(UUID::class)
        every { UUID.randomUUID().toString() } returns "abc"
        val response = userService.authorizeUser(newRequest)
        response shouldBe "abc"
        tokenRepository.findByToken("abc")!!.user shouldBe user
    }

    @Test
    fun `Изменение пользователя, неверный пароль`() {
        val newRequest = ManageRequest(
            userName = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            password = "TRALALELOTRALALA",
            oldPassword = "123")
        val user =UserEntity(
            username = newRequest.userName,
            email = newRequest.email,
            phone = newRequest.phone,
            password = "12345",
            tg = "@TUNGTUNGTUNGTUNGSAHUUUR"
        )
        userRepository.save(user)
        tokenRepository.save(TokenEntity(token ="token", user=user))
        shouldThrow<WrongPasswordException> {
            userService.manageUser("token", newRequest)
        }
    }

    @Test
    fun `Изменение пользователя, успешное выполнение`() {
        val newRequest = ManageRequest(
            userName = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            password = "TRALALELOTRALALA",
            oldPassword = "123")
        val user =UserEntity(
            username = newRequest.userName,
            email = newRequest.email,
            phone = newRequest.phone,
            password = "123",
            tg = "@TUNGTUNGTUNGTUNGSAHUUUR"
        )
        userRepository.save(user)
        tokenRepository.save(TokenEntity(token ="token", user=user))
        shouldNotThrow<WrongPasswordException> {
            userService.manageUser("token", newRequest)
        }
        tokenRepository.findByToken("token")!!.user shouldBe UserEntity(
            id=1,
            username = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            password = "TRALALELOTRALALA",
            tg = "@TUNGTUNGTUNGTUNGSAHUUUR")
    }

    @Test
    fun `Выход пользователя из аккаунта, успешное выполнение`() {
        val user =UserEntity(
            username = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            password = "123",
            tg = "@TUNGTUNGTUNGTUNGSAHUUUR"
        )
        userRepository.save(user)
        tokenRepository.save(TokenEntity(token="token", user=user))
        shouldNotThrow<NotValidTokenException> {
            userService.logout("token")
        }
        tokenRepository.findByToken("token")!!.revoked shouldBe true
    }

    @Test
    fun `Удаление аккаунта пользователя, неверный пароль`() {
        val user = UserEntity(
            username = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            password = "123",
            tg = "@TUNGTUNGTUNGTUNGSAHUUUR"
        )
        userRepository.save(user)
        tokenRepository.save(TokenEntity(token="token", user=user))
        shouldThrow<WrongPasswordException> { userService.deleteUser("token", "TRALALELOTRALALA") }
    }

    @Test
    fun `Удаление аккаунта пользователя, успешное выполнение`() {
        val user = UserEntity(
            username = "Bob",
            email = "jdfjgd@mail.ru",
            phone = "789573542874",
            password = "TRALALELOTRALALA",
            tg = "@TUNGTUNGTUNGTUNGSAHUUUR"
        )
        userRepository.save(user)
        tokenRepository.save(TokenEntity(token="token", user=user))
        shouldNotThrow<WrongPasswordException> { userService.deleteUser("token", "TRALALELOTRALALA") }
        tokenRepository.findByToken("token")!!.user.active shouldBe false
    }
}