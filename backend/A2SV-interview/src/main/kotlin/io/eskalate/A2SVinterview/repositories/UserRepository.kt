package io.eskalate.A2SVinterview.repositories

import io.eskalate.A2SVinterview.models.User
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component

interface UserRepository: ReactiveMongoRepository<User, String>, CustomUserRepository

interface CustomUserRepository {
    suspend fun update(user: User): User
    suspend fun findByUsername(username: String): User?
}

@Component
class UserRepositoryImpl(val mongoOperations: ReactiveFluentMongoOperations): CustomUserRepository {
    override suspend fun update(user: User): User =
            mongoOperations.update<User>()
                    .matching(Query.query(Criteria.where("username").isEqualTo(user.username)))
                    .replaceWith(user)
                    .asType<User>()
                    .findReplaceAndAwait()

    override suspend fun findByUsername(username: String): User? =
            mongoOperations.query<User>()
                    .matching(Query.query(Criteria.where("username").isEqualTo(username)))
                    .awaitFirstOrNull()
}