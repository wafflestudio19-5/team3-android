package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
class Follow(
    @Json(name = "id")
    val id : Long,
    @Json(name = "user")
    val user: User,
    @Json(name = "createdAt")
    val createdAt : LocalDateTime
)