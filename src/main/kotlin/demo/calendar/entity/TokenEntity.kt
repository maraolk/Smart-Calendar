package demo.calendar.entity

import jakarta.persistence.*

@Entity
@Table(name = "tokens")
data class TokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val token: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    val revoked: Boolean = false
)