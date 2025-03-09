package demo.calendar.repository

import demo.calendar.dto.UserToCalendar
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserToCalendarRepository: JpaRepository<UserToCalendar, Long> {
}