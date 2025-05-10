package demo.calendar.dto

data class CalendarResponse(
    val calendarName: String,
    val isPublic: Boolean,
    val teg: String,
    val active: Boolean,
    val description: String,
)
