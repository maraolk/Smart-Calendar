package demo.calendar.dto

data class CalendarResponse(
    val calendarName: String,
    val public: Boolean,
    val teg: String,
    val active: Boolean,
    val description: String,
)
