package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.User
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @GET("/api/v1/users/me/")
    suspend fun getMyInfo(): User

    @POST("/api/v1/users/profile/")
    suspend fun getInfoByUsername(username: String): User
}