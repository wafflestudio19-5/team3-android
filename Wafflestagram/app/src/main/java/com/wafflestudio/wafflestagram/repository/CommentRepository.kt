package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.CommentService
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(private val commentService: CommentService) {

    suspend fun getComments(id : Int) : Response<List<Comment>>{
        return commentService.getComments(id)
    }
    suspend fun addComment(id: Int, addCommentRequest: AddCommentRequest) : Response<Comment>{
        return commentService.addComment(id, addCommentRequest)
    }
    suspend fun getFeedById(id : Int): Response<Feed>{
        return commentService.getFeedById(id)
    }
}