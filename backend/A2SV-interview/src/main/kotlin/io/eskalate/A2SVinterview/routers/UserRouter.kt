package io.eskalate.A2SVinterview.routers

import io.eskalate.A2SVinterview.controllers.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter {

    @Bean
    fun routes(userHandler: UserHandler) = coRouter {
        "/user".nest {
            GET("/", userHandler::findAll)
            GET("/{id}", userHandler::findOne)
            POST("/", userHandler::insert)
            DELETE("/{id}", userHandler::delete)
            PUT("/", userHandler::update)
        }
    }
}