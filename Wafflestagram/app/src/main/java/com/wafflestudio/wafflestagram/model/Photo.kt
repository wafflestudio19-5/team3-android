package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Photo (
    @Json(name = "id")
    val id: Int,
    @Json(name = "s3path")
    val path: String
    ){
}