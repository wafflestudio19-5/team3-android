package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.network.FeedService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(private val feedService: FeedService){

}