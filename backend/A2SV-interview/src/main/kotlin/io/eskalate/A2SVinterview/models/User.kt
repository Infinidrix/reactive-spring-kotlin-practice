package io.eskalate.A2SVinterview.models

import org.springframework.data.annotation.Id
import java.sql.Date

enum class Gender {
    MALE,
    FEMALE,
    UNDISCLOSED
}

data class User(
        @Id
        var id: String,
        var username: String,
        var password: String,
        var firstName: String? = null,
        var surname: String? = null,
        var gender: Gender? = null,
        var birthday: Date? = null
)