package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentService {

    @GET("api/v1/comment/list/{feed_id}/")
    suspend fun getComments(@Path("feed_id") id: Int): Response<List<Comment>>

    @GET("api/v1/feed/{feed_id}/")
    suspend fun getFeedById(@Path("feed_id") id: Int): Response<Feed>

    @POST("api/v1/comment/{feed_id}/")
    suspend fun addComment(@Path("feed_id") id: Int, @Body addCommentRequest: AddCommentRequest) : Response<Comment>
}