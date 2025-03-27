package service

import com.ninjasquad.springmockk.MockkBean
import demo.calendar.controller.UserController
import demo.calendar.dto.SingUpRequest
import demo.calendar.entity.UserEntity
import demo.calendar.exception.UserAlreadyRegisteredException
import demo.calendar.repository.UserRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Test

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {
}