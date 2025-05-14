package demo.calendar.repository

import demo.calendar.entity.CalendarEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CalendarRepository: JpaRepository<CalendarEntity, Long> {
    fun findByTeg(teg: String): CalendarEntity?
    fun findByPublic(public: Boolean, pageable: Pageable): Page<CalendarEntity>
}