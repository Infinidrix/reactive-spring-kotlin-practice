package io.eskalate.A2SVinterview.controllers

import com.mongodb.client.result.DeleteResult
import io.eskalate.A2SVinterview.models.User
import io.eskalate.A2SVinterview.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait

@RestController @RequestMapping("/user")
@EnableWebFlux
class UserController (private val userRepository: UserRepository){

    @GetMapping("/")
    suspend fun findAll(): Flow<User> =
            userRepository.findAll()

    @GetMapping("/{id}")
    suspend fun findOne(@PathVariable id: String): User? =
            userRepository.findOne(id)

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun insert(@RequestBody user: User): User =
            userRepository.insert(user)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun delete(@PathVariable id: String): DeleteResult =
            userRepository.delete(id)

    @PutMapping("/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun update(@RequestBody user: User): User =
            userRepository.update(user)
}