package demo.calendar.entity

import demo.calendar.dto.CalendarResponse
import jakarta.persistence.*

//в календаре у нас буквально 1 поле по которому мы можем искать, поэтому реализуем быстрый поиск по нему
@Entity
@Table(name = "calendars", indexes = [Index(name = "idx_calendars_name", columnList = "calendar_name")])
data class CalendarEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val calendar_name: String,

    @Column(nullable=false)
    val public: Boolean = false,

    @Column(nullable=false)
    val active: Boolean = true,

    @Column(nullable=false, unique=true)
    val teg: String,

    @Column
    val description: String
) {
    fun toCalendar() = CalendarResponse(
        calendarName = calendar_name,
        public = public,
        active = active,
        teg = teg,
        description = description)
}
