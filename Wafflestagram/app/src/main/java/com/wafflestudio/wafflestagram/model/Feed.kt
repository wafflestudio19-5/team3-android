package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Feed (
    @Json(name = "id")
    val id: Int,
    @Json(name = "writer")
    val writer: String,
    @Json(name = "content")
    val content: String,
    @Json(name = "photos")
    val photos: List<Photo>,
    @Json(name = "comments")
    val comments: List<Comment>
){
}