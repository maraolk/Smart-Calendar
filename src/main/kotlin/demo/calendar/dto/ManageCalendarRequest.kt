package demo.calendar.dto

data class ManageCalendarRequest(
    val calendarId: Long,
    val operation: Operation,
    val newCalendar: UpdateCalendar?
)
{
    enum class Operation{DELETE, UPDATE}
}