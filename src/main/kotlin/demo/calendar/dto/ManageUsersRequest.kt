package demo.calendar.dto

data class ManageUsersRequest(
    val calendarTeg: String,
    val userTg: String,
    val accessType: String
)