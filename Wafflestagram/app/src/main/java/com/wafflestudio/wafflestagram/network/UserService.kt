package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @GET("/api/v1/users/me/")
    suspend fun getMyInfo(): Response<User>

    @GET("/api/v1/users/followerNum/")
    suspend fun getMyFollowerCount(): Response<ResponseBody>

    @GET("/api/v1/users/followingNum/")
    suspend fun getMyFollowingCount(): Response<ResponseBody>

    @GET("/api/v1/users/feedsNum/")
    suspend fun getMyFeedsCount(): Response<ResponseBody>

    @GET("/api/v1/users/profile/{user_id}/")
    suspend fun getInfoByUserId(
        @Path("user_id") userId: Int
    ): Response<User>

    @GET("/api/v1/users/followerNum/{user_id}/")
    suspend fun getFollowerCountByUserId(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @GET("/api/v1/users/followingNum/{user_id}/")
    suspend fun getFollowingCountByUserId(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @GET("/api/v1/users/feedsNum/{user_id}")
    suspend fun getFeedCountByUserId(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @POST("/api/v1/users/profile/")
    fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    ): Response<ResponseBody>
}