package io.eskalate.A2SVinterview.controllers

import io.eskalate.A2SVinterview.models.User
import io.eskalate.A2SVinterview.repositories.UserRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest @Autowired constructor(
        val client: WebTestClient,
        val repository: UserRepository
){
    // TODO: Add failing tests
    lateinit var users: List<User>
    @BeforeEach
    fun setUp(){
        runBlocking {
            users = createUsers(5)
            repository.saveAll(users).awaitLast()
        }
    }

    @AfterEach
    fun tearDown(){
        runBlocking {
            repository.deleteAll(users).awaitFirstOrNull()
        }
    }
    fun createUsers(amount: Int): List<User> {
        val users = mutableListOf<User>()
        for (i in 1..amount){
            users.add(User(
                    UUID.randomUUID().toString(),
                    "BirukSolomon",
                    "SuperStrongPassword"
            ))
        }
        return users
    }


    @Test
    fun findAll() {
        runBlocking {
            client.get()
                    .uri("/user/")
                    .exchange()
                    .expectBodyList<User>()
                    .contains(users[0])

        }
    }

    @Test
    fun findOne() {
        runBlocking {
            client.get()
                    .uri("/user/${users[0].id}")
                    .exchange()
                    .expectBody<User>()
                    .isEqualTo(users[0])
        }
    }

    @Test
    fun insert() {
        runBlocking {
            val newUser = User(
                    UUID.randomUUID().toString(),
                    "RandomPerson1",
                    "ForgottenPassword"
            )
            try {
                client.post()
                        .uri("/user/")
                        .bodyValue(newUser)
                        .exchange()
                        .expectStatus().isCreated
                assertThat(repository.findByUsername(newUser.username)).isNotNull
            } catch (e: Exception){
                throw e
            }
            finally {
                repository.deleteById(newUser.id).awaitFirstOrNull()
            }

        }
    }

    @Test
    fun delete() {
        runBlocking {
            val newUser = User(
                    UUID.randomUUID().toString(),
                    "RandomPerson2",
                    "ForgottenPassword"
            )
            repository.save(newUser).awaitFirst()
            try{
                client.delete()
                        .uri("/user/${newUser.id}")
                        .exchange()
                        .expectStatus().isAccepted
                assertThat(repository.findById(newUser.id).awaitFirstOrNull()).isNull()
            } catch (e: Exception){
                repository.deleteById(newUser.id).awaitFirst()
                throw e
            }

        }
    }

    @Test
    fun update() {
        runBlocking {
            val newUser = User(
                    UUID.randomUUID().toString(),
                    "RandomPerson3",
                    "ForgottenPassword"
            )
            repository.save(newUser).awaitFirst()
            newUser.password = "ToBeForgottenPassword"
            try {
                client.put()
                        .uri("/user/")
                        .bodyValue(newUser)
                        .exchange()
                        .expectStatus().isAccepted
                assertThat(repository.findByUsername(newUser.username)).isEqualTo(newUser)
            } catch (e: Exception) {
                throw e
            } finally {
                repository.deleteById(newUser.id).awaitFirstOrNull()
            }
        }
    }
}