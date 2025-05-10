package demo.calendar.dto

data class CreateCalendarRequest(
    val calendarName: String,
    val isPublic: Boolean,
    val teg: String,
    val description: String,
)