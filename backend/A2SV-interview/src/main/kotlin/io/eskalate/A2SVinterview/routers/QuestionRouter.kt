package io.eskalate.A2SVinterview.routers

import io.eskalate.A2SVinterview.controllers.QuestionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class QuestionRouter() {

    @Bean
    fun questionRoutes(questionHandler: QuestionHandler) = coRouter {
        "/question".nest {
            GET("/", questionHandler::findAll)
            GET("/{id}", questionHandler::findOne)
            POST("/", questionHandler::insert)
            DELETE("/{id}", questionHandler::delete)
            PUT("/", questionHandler::update)
        }
    }
}