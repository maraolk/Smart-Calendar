package demo.calendar.dto

data class ManageUsersRequest(
    val calendarId: Long,
    val userTg: String,
    val operation: Operation
)
{
    enum class Operation{ADD, DELETE, VIEWER, MODERATOR, ADMIN}
}
