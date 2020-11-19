package io.eskalate.A2SVinterview.repositories

import io.eskalate.A2SVinterview.models.Question
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations
import org.springframework.data.mongodb.core.asType
import org.springframework.data.mongodb.core.findReplaceAndAwait
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.update
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component

interface QuestionsRepository: ReactiveMongoRepository<Question, String>, CustomQuestionsRepository

interface CustomQuestionsRepository {
    suspend fun update(question: Question): Question
}

@Component
class QuestionsRepositoryImpl(val mongoOperations: ReactiveFluentMongoOperations): CustomQuestionsRepository{
    override suspend fun update(question: Question): Question =
        mongoOperations.update<Question>()
                .matching(Query.query(Criteria.where("id").isEqualTo(question.id)))
                .replaceWith(question)
                .asType<Question>()
                .findReplaceAndAwait()
}