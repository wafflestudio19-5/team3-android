package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.UserPage
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SearchService {

    @GET("api/v1/users/search/{nickname}/")
    suspend fun search(@Path("nickname") nickname: String) : Response<UserPage>

    @GET("api/v1/users/following/{user_id}/")
    suspend fun checkFollowing(@Path("user_id") id: Int): Response<Boolean>

    @POST("api/v1/users/follow/{user_id}/")
    suspend fun follow(@Path("user_id") id: Int)

    @DELETE("api/v1/users/unfollow/{user_id}/")
    suspend fun unfollow(@Path("user_id") id: Int)

}