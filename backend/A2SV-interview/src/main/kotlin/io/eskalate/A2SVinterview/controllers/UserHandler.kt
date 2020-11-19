package io.eskalate.A2SVinterview.controllers

import io.eskalate.A2SVinterview.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.net.URI

@Component
class UserHandler (private val userRepository: UserRepository){

    suspend fun findAll(request: ServerRequest) =
            ok().bodyAndAwait(userRepository.findAll())

    suspend fun findOne(request: ServerRequest) =
            userRepository.findOne(request.pathVariable("id"))
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: notFound().buildAndAwait()

    suspend fun insert(request: ServerRequest) =
            userRepository.insert(request.awaitBody())
                    .let { created(URI("/user/" + it.id)).bodyValueAndAwait(it)}

    suspend fun delete(request: ServerRequest) =
            userRepository.delete(request.pathVariable("id"))
                    .let {
                        if (it.deletedCount > 0) accepted().buildAndAwait()
                        else notFound().buildAndAwait()
                    }

    suspend fun update(request: ServerRequest) =
            userRepository.update(request.awaitBody())
                    . let { accepted().bodyValueAndAwait(it) }
}
