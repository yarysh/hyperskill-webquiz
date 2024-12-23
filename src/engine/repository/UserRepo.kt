package engine.repository

import engine.model.User
import org.springframework.data.repository.CrudRepository


interface UserRepo : CrudRepository<User, Int> {
    fun findUserByUsername(username: String): User
}
