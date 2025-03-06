package demo.calendar.dto

data class ManageEventRequest(
    val eventId: Long,
    val operation: Operation,
    val newEvent: UpdateEvent?
)
{
    enum class Operation{DELETE, UPDATE}
}
