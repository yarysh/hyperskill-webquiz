package engine.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.Instant.now


@Entity
@Table(name = "completions")
class Completion(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int?,

    @NotNull
    var quizId: Int,

    @NotNull
    var completedBy : String,

    @Column(name = "completedAt")
    var completedAt: String = now().toString(),
)
