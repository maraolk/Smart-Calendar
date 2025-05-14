package demo.calendar.dto

data class ManageCalendarRequest(
    val calendarName: String,
    val public: Boolean,
    val teg: String,
    val description: String,
    val active: Boolean
)