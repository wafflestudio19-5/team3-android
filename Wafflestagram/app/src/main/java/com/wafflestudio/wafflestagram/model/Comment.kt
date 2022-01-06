package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Comment (
    @Json(name = "id")
    val id: Int,
    @Json(name = "writer")
    val writer: String,
    @Json(name = "text")
    val text: String,
    @Json(name = "replies")
    val replies: List<Reply>
    //createdAt
)