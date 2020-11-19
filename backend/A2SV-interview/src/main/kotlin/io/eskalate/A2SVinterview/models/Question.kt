package io.eskalate.A2SVinterview.models

import org.springframework.data.annotation.Id

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

data class Question (
        @Id
        var id: String,
        var title: String,
        var link: String,
        var rating: Int = 0,
        var tags: List<String>? = null,
        var difficulty: Difficulty? = null,
        var company: List<String>? = null
)