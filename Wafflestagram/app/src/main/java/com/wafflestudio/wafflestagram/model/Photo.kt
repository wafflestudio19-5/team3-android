package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Photo (
    @Json(name = "key")
    val id: Long = 0,
    @Json(name = "url")
    val path: String = ""
){
}