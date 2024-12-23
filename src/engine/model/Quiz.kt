package engine.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size


@Entity
@Table(name = "quizzes")
class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int?,

    @NotEmpty
    var title: String,

    @NotEmpty
    var text: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @Size(min = 2)
    var options: List<String>,

    @ElementCollection(fetch = FetchType.EAGER)
    val answer: List<Int>?,

    @NotEmpty
    var createdBy: String,
)
