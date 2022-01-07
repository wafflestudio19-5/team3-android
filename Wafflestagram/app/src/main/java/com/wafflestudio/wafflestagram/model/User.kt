package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class User(
    @Json(name = "id")
    val id: Long,
    @Json(name = "username")
    val username: String,
    @Json(name = "email")
    val email: String
)