package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
class Feed (
    @Json(name = "id")
    val id: Long,
    @Json(name = "writer")
    val writer: String = "",
    @Json(name = "content")
    val content: String = "",
    @Json(name = "photos")
    val photos: List<Photo> = emptyList(),
    @Json(name = "comments")
    val comments: List<Comment> = emptyList(),
    @Json(name = "createdAt")
    val createdAt : LocalDateTime = LocalDateTime.now(),
    @Json(name = "updatedAt")
    val updatedAt : LocalDateTime = LocalDateTime.now()
    ){
}