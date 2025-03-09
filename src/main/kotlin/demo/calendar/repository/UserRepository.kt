package demo.calendar.repository

import demo.calendar.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<User, Long> {
    fun findByPhone(phone: String): User?

    fun findByTg(tg: String): User?
}