package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
class Comment (
    @Json(name = "id")
    val id: Long,
    @Json(name = "writer")
    val writer: String,
    @Json(name = "text")
    val text: String,
    @Json(name = "replies")
    val replies: List<Reply>,
    val createdAt : LocalDateTime = LocalDateTime.now(),
    @Json(name = "updatedAt")
    val updatedAt : LocalDateTime = LocalDateTime.now()
)