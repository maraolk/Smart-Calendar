package demo.calendar.repository

import demo.calendar.entity.CalendarEntity
import demo.calendar.entity.UserEntity
import demo.calendar.entity.UserToCalendarEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserToCalendarRepository: JpaRepository<UserToCalendarEntity, Long> {
    fun findByUser(user: UserEntity): List<UserToCalendarEntity>

    fun findByUserAndCalendar(user: UserEntity, calendar: CalendarEntity): UserToCalendarEntity?
}