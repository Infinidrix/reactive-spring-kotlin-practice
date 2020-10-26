package io.eskalate.A2SVinterview.repositories

import com.mongodb.client.result.DeleteResult
import io.eskalate.A2SVinterview.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class UserRepository(private val mongo: ReactiveFluentMongoOperations){

    fun findAll(): Flow<User> =
            mongo.query<User>().flow()

    suspend fun findOne(id: String): User? =
            mongo.query<User>()
                    .matching(query(where("id").isEqualTo(id)))
                    .awaitOneOrNull()

    suspend fun findByUsername(username: String): User? =
            mongo.query<User>()
                    .matching(query(where("username").isEqualTo(username)))
                    .awaitOneOrNull()

    suspend fun insert(user: User): User =
            mongo.insert<User>().oneAndAwait(user)

    suspend fun update(user: User): User =
            mongo.update<User>()
                    .matching(query(where("username").isEqualTo(user.username)))
                    .replaceWith(user)
                    .asType<User>()
                    .findReplaceAndAwait()

    suspend fun delete(id: String): DeleteResult =
            mongo.remove<User>()
                    .matching(query(where("id").isEqualTo(id)))
                    .allAndAwait()
}