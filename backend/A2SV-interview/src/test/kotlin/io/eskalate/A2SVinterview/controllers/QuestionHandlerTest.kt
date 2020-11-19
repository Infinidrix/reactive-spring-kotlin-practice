package io.eskalate.A2SVinterview.controllers

import io.eskalate.A2SVinterview.models.Difficulty
import io.eskalate.A2SVinterview.models.Question
import io.eskalate.A2SVinterview.models.User
import io.eskalate.A2SVinterview.repositories.QuestionsRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionHandlerTest @Autowired constructor(
        val client: WebTestClient,
        val repository: QuestionsRepository
){
    // TODO: Add failing tests
    lateinit var questions: List<Question>
    @BeforeEach
    fun setUp() {
        runBlocking {
            questions = createQuestions(5)
            repository.saveAll(questions).awaitLast()
        }
    }

    @AfterEach
    fun tearDown() {
        runBlocking {
            repository.deleteAll(questions).awaitFirstOrNull()
        }
    }

    fun createQuestions(amount: Int): List<Question> {
        val questions = mutableListOf<Question>()
        (1..amount).forEach { _ ->
            questions.add(Question(
                    UUID.randomUUID().toString(),
                    "Hardest Question Ever",
                    "https://leetcode.com/problems/strong-password-checker",
                    difficulty = Difficulty.HARD
            ))
        }
        return questions
    }

    @Test
    fun findAll() {
        runBlocking {
            client.get()
                    .uri("/question/")
                    .exchange()
                    .expectBodyList<Question>()
                    .contains(questions[0])
        }
    }

    @Test
    fun findOne() {
        runBlocking {
            client.get()
                    .uri("/question/${questions[0].id}")
                    .exchange()
                    .expectBody<Question>()
                    .isEqualTo(questions[0])
        }
    }

    @Test
    fun insert() {
        runBlocking {
            val newQuestion = Question(
                    UUID.randomUUID().toString(),
                    "Another Hard Question",
                    "https://leetcode.com/problems/split-array-with-same-average"
            )
            try {
                client.post()
                        .uri("/question/")
                        .bodyValue(newQuestion)
                        .exchange()
                        .expectStatus().isCreated
                AssertionsForClassTypes.assertThat(repository.findById(newQuestion.id).awaitFirstOrNull()).isNotNull
            } catch (e: Exception){
                throw e
            }
            finally {
                repository.deleteById(newQuestion.id).awaitFirstOrNull()
            }
        }
    }

    @Test
    fun delete() {
        runBlocking {
            val newQuestion = Question(
                    UUID.randomUUID().toString(),
                    "Yet Another Hard Question",
                    "https://leetcode.com/problems/maximum-number-of-visible-points/"
            )
            repository.save(newQuestion).awaitFirst()
            try {
                client.delete()
                        .uri("/question/" + newQuestion.id)
                        .exchange()
                        .expectStatus().isAccepted
                AssertionsForClassTypes.assertThat(repository.findById(newQuestion.id).awaitFirstOrNull()).isNull()
            } catch (e: Exception) {
                repository.deleteById(newQuestion.id).awaitFirstOrNull()
                throw e
            }
        }
    }

    @Test
    fun update() {
        runBlocking {
            val newQuestion = Question(
                    UUID.randomUUID().toString(),
                    "Yet Another Hard Question",
                    "https://leetcode.com/problems/split-array-with-same-average"
            )
            repository.save(newQuestion).awaitFirst()
            newQuestion.link = "https://leetcode.com/problems/max-points-on-a-line"
            try {
                client.put()
                        .uri("/question/")
                        .bodyValue(newQuestion)
                        .exchange()
                        .expectStatus().isAccepted
                AssertionsForClassTypes.assertThat(repository.findById(newQuestion.id).awaitFirstOrNull()).isEqualTo(newQuestion)
            } catch (e: Exception) {
                throw e
            } finally {
                repository.deleteById(newQuestion.id).awaitFirstOrNull()
            }
        }
    }
}