package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.network.FeedService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailFeedRepository @Inject constructor(private val feedService: FeedService){

    suspend fun getFeedsByUserId(id: Int, offset : Int, limit: Int):Response<Page>{
        return feedService.getFeedsByUserId(id, offset, limit)
    }
}