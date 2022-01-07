package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @GET("/api/v1/users/me/")
    suspend fun getMyInfo(): User

    @GET("/api/v1/users/profile/{user_id}")
    suspend fun getInfoByUserId(
        @Path("user_id") userId: Long
    ): User
}