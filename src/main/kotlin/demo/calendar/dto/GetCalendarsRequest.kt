package demo.calendar.dto

data class GetCalendarsRequest(
    val page: Int,
    val size: Int,
    val sortBy: String?,
    val type: String?  //PUBLIC, ALLOWED, OWN
)