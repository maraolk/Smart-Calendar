package demo.calendar.dto

data class CreateCalendarRequest(
    val calendarName: String,
    val public: Boolean,
    val teg: String,
    val description: String,
)