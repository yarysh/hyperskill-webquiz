package engine.controller

import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*

import engine.model.*
import engine.repository.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size


@RestController
class QuizController(
    val completionRepo: CompletionRepo,
    val passwordEncoder: PasswordEncoder,
    val quizRepo: QuizRepo,
    val userRepo: UserRepo,
) {
    class RegisterRequest(
        @field:Email(regexp = ".+@.+\\..+")
        val email: String,

        @field:Size(min = 5)
        val password: String
    )
    
    @PostMapping("/api/register")
    fun postRegister(@Valid @RequestBody req: RegisterRequest, err: Errors): ResponseEntity<Any> {
        if (err.hasErrors()) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        try {
            userRepo.save(User(
                null,
                username=req.email,
                password=passwordEncoder.encode(req.password),
            ))
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    class QuizRequest(
        @field:NotEmpty
        val title: String = "",

        @field:NotEmpty
        val text: String = "",

        @field:Size(min = 2)
        val options: Array<String>,

        val answer: Array<Int> = emptyArray(),
    )

    @PostMapping("/api/quizzes")
    fun postQuizzes(@Valid @RequestBody quizRequest: QuizRequest, err: Errors, auth: Authentication): ResponseEntity<Any> {
        if (err.hasErrors()) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val quiz = quizRepo.save(Quiz(
            null,
            quizRequest.title,
            quizRequest.text,
            quizRequest.options.toList(),
            quizRequest.answer.toList(),
            auth.name,
        ))

        return ResponseEntity<Any>(mapOf(
            "id" to quiz.id,
            "title" to quiz.title,
            "text" to quiz.text,
            "options" to quiz.options,
        ), HttpStatus.OK)
    }

    @GetMapping("/api/quizzes")
    fun getQuizzes(@RequestParam(defaultValue = "0") page: Int): ResponseEntity<Any> {
        val pageable = PageRequest.of(page, 10)
        val quizzes = quizRepo.findAll(pageable).map { quiz ->
            mapOf(
                "id" to quiz.id,
                "title" to quiz.title,
                "text" to quiz.text,
                "options" to quiz.options
            )
        }

        return ResponseEntity<Any>(quizzes, HttpStatus.OK)
    }

    @GetMapping("/api/quizzes/{id}")
    fun getQuizzesById(@PathVariable id: Int): ResponseEntity<Any> {
        val quiz = quizRepo.findById(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity<Any>(mapOf(
            "id" to quiz.id,
            "title" to quiz.title,
            "text" to quiz.text,
            "options" to quiz.options,
        ), HttpStatus.OK)
    }

    @DeleteMapping("/api/quizzes/{id}")
    fun deleteQuizzesById(@PathVariable id: Int, auth: Authentication): ResponseEntity<Any> {
        val quiz = quizRepo.findById(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        if (quiz.createdBy != auth.name) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        
        quizRepo.deleteById(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/api/quizzes/{quizId}/solve")
    fun postSolveQuiz(@PathVariable quizId: Int, @RequestBody req: Map<String, Array<Int>>, auth: Authentication): ResponseEntity<Any> {
        val quiz = quizRepo.findById(quizId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val resp: Map<String, Any>

        if (!req.getOrDefault("answer", emptyArray()).contentEquals(quiz.answer?.toTypedArray())) {
            resp = mapOf(
                "success" to false,
                "feedback" to "Wrong answer! Please, try again."
            )
        } else {
            completionRepo.save(Completion(
                id=null,
                completedBy=auth.name,
                quizId=quizId
            ))
            resp = mapOf(
                "success" to true,
                "feedback" to "Congratulations, you're right!"
            )
        }

        return ResponseEntity<Any>(resp, HttpStatus.OK)
    }

    @GetMapping("/api/quizzes/completed")
    fun getQuizzesCompleted(@RequestParam(defaultValue = "0") page: Int, auth: Authentication): ResponseEntity<Any> {
        val pageable = PageRequest.of(page, 10)
        val completions = completionRepo.findAllByCompletedByOrderByCompletedAtDesc(auth.name, pageable).map {
            mapOf(
                "id" to it.quizId,
                "completedAt" to it.completedAt,
            )
        }

        return ResponseEntity<Any>(completions, HttpStatus.OK)
    }
}
