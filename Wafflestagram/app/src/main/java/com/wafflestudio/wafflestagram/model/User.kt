package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class User(
    @Json(name = "bio")
    val bio: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "id")
    val id: Long,
    @Json(name = "name")
    val name: String?,
    @Json(name = "nickname")
    val username: String,
    @Json(name = "profilePhotoURL")
    val profilePhotoURL: String?,
    @Json(name = "public")
    val public: Boolean,
    @Json(name = "website")
    val website: String?
){
    override fun equals(other: Any?): Boolean {
        return if(other is User)
            other.id == this.id
        else
            false
    }
}