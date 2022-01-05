package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Like (
    @Json(name = "id")
    val id: Int,
    @Json(name = "writer")
    val writer: String
    )