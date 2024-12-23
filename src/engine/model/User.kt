package engine.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty


@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int?,

    @Column(unique = true)
    var username: String,

    @NotEmpty
    var password: String,
)
