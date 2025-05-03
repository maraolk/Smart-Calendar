package demo.calendar.dto

data class CreateCalendarRequest(
    val calendarName: String,
    val isPublic: Boolean
)