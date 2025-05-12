package demo.calendar.repository

import demo.calendar.entity.CalendarEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CalendarRepository: JpaRepository<CalendarEntity, Long> {
    fun findByTeg(teg: String): CalendarEntity?
    fun findByIsPublic(isPublic: Boolean, pageable: Pageable): Page<CalendarEntity>
}