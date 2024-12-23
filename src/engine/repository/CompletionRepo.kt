package engine.repository

import engine.model.Completion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository


interface CompletionRepo : PagingAndSortingRepository<Completion, Int> {
    fun save(completion: Completion): Completion
    fun findAllByCompletedByOrderByCompletedAtDesc(username: String, pageable: Pageable): Page<Completion>
}