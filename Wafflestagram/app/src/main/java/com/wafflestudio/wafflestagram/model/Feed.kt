package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
class Feed (
    @Json(name = "id")
    val id: Long,
    @Json(name = "author")
    val author: User? = null,
    @Json(name = "content")
    val content: String = "",
    @Json(name = "photos")
    val photos: List<Photo> = emptyList(),
    @Json(name = "comments")
    val comments: List<Comment> = emptyList(),
    @Json(name = "likes")
    val likes: List<User> = emptyList(),
    @Json(name = "likeSum")
    val likeSum: Int = 0,
    @Json(name = "tags")
    val tags: List<String> = emptyList(),
    @Json(name = "user_tags")
    val user_tags: List<String> = emptyList(),
    @Json(name = "created_at")
    val createdAt : LocalDateTime? = LocalDateTime.now(),
    @Json(name = "updatedAt")
    val updatedAt : LocalDateTime? = LocalDateTime.now()
    ){
}