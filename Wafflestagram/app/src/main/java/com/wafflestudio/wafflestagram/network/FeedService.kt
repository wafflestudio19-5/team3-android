package com.wafflestudio.wafflestagram.network

import androidx.room.Delete
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.dto.AddPostRequest
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import retrofit2.Response
import retrofit2.http.*

interface FeedService {

    @POST("api/v1/feed/")
    suspend fun addPost(@Body addPostRequest: AddPostRequest): Response<Feed>

    @GET("api/v1/feed/")
    suspend fun getFeeds(@Query("offset") offset: Int, @Query("limit") limit: Int) : Response<List<Feed>>

    @GET("api/v1/feed/{feed_id}/")
    suspend fun getFeedById(@Path("feed_id") id: Int): Response<Feed>

    @POST("api/v1/feed/like/{feed_id}/")
    suspend fun like(@Path("feed_id") id :Int): Response<Feed>

    @DELETE("api/v1/feed/like/{feed_id}/")
    suspend fun unlike(@Path("feed_id") id: Int): Response<Feed>
}