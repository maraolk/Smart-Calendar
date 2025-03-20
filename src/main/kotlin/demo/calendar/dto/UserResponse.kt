package demo.calendar.dto

data class UserResponse(
    val userName: String,
    val phone: String?,
    val email: String?,
    val tg: String
)