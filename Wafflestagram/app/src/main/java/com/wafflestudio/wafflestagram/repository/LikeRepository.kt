package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.FeedService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeRepository @Inject constructor(private val feedService: FeedService) {

    suspend fun getFeedById(id : Int): Response<Feed> {
        return feedService.getFeedById(id)
    }

}