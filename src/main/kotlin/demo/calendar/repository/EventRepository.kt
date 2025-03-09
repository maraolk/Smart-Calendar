package demo.calendar.repository

import demo.calendar.dto.Event
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventRepository: JpaRepository<Event, Long> {
    fun findByTitle(title: String): List<Event>
}