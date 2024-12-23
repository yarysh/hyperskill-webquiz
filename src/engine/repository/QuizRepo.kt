package engine.repository

import engine.model.Quiz
import org.springframework.data.repository.PagingAndSortingRepository


interface QuizRepo : PagingAndSortingRepository<Quiz, Int> {
    fun findById(id: Int): Quiz?
    fun deleteById(id: Int)
    fun save(quiz: Quiz): Quiz
}
