package demo.calendar.repository

import demo.calendar.entity.TokenEntity
import demo.calendar.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository: JpaRepository<TokenEntity, Long> {
    fun findByUser(user: UserEntity): List<TokenEntity>

    fun findByValue(value: String): TokenEntity?
}