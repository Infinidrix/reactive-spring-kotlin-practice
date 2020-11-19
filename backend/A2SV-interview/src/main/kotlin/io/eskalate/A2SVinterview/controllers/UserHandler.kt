package io.eskalate.A2SVinterview.controllers

import io.eskalate.A2SVinterview.models.User
import io.eskalate.A2SVinterview.repositories.UserRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.lang.IllegalArgumentException
import java.net.URI

@Component
class UserHandler (private val userRepository: UserRepository){
    // TODO: Hash Password
    // TODO: Auth
    // TODO: Unique Username
    suspend fun findAll(request: ServerRequest) =
            ok().bodyAndAwait(userRepository.findAll().asFlow())

    suspend fun findOne(request: ServerRequest) =
            userRepository.findById(request.pathVariable("id"))
                    .awaitFirstOrNull()
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: notFound().buildAndAwait()

    suspend fun insert(request: ServerRequest) =
            userRepository.save(request.awaitBody<User>())
                    .awaitFirstOrNull()
                    ?.let { created(URI("/user/" + it.id)).bodyValueAndAwait(it)}
                    ?: badRequest().buildAndAwait()

    suspend fun delete(request: ServerRequest) =
        try {
            userRepository.deleteById(request.pathVariable("id"))
                    .awaitFirstOrNull()
                    .let {
                        accepted().buildAndAwait()
                    }
        } catch (err: IllegalArgumentException) {
            badRequest().buildAndAwait()
        }


    suspend fun update(request: ServerRequest) =
        userRepository.update(request.awaitBody())
                .let { accepted().bodyValueAndAwait(it) }
}
