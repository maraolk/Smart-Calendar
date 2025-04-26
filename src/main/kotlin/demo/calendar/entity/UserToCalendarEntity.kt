package demo.calendar.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_to_calendar")
data class UserToCalendarEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: CalendarEntity,

    val access_type: String
)