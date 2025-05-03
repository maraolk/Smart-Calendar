package demo.calendar.dto

data class ManageCalendarRequest(
    val calendarName: String,
    val isPublic: Boolean
)