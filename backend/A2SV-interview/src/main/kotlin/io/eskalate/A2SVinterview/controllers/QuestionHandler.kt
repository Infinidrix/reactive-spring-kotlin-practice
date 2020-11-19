package io.eskalate.A2SVinterview.controllers

import io.eskalate.A2SVinterview.models.Question
import io.eskalate.A2SVinterview.repositories.QuestionsRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.lang.IllegalArgumentException
import java.net.URI

@Component
class QuestionHandler(private val questionsRepository: QuestionsRepository){

    suspend fun findAll(request: ServerRequest): ServerResponse =
            ok().bodyAndAwait(questionsRepository.findAll().asFlow())

    suspend fun findOne(request: ServerRequest): ServerResponse =
            questionsRepository
                    .findById(request.pathVariable("id"))
                    .awaitFirstOrNull()
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: badRequest().buildAndAwait()

    suspend fun insert(request: ServerRequest) =
            questionsRepository
                    .save(request.awaitBody<Question>())
                    .awaitFirstOrNull()
                    ?.let { created(URI("/question/" + it.id)).bodyValueAndAwait(it) }
                    ?: badRequest().buildAndAwait()

    suspend fun delete(request: ServerRequest) =
            try {
                questionsRepository
                        .deleteById(request.pathVariable("id"))
                        .awaitFirstOrNull()
                        .let {
                            accepted().buildAndAwait()
                        }
            } catch (err: IllegalArgumentException) {
                badRequest().buildAndAwait()
            }


    suspend fun update(request: ServerRequest): ServerResponse =
            questionsRepository.update(request.awaitBody())
                    .let { accepted().bodyValueAndAwait(it) }
}