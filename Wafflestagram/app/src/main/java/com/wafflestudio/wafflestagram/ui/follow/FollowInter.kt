package com.wafflestudio.wafflestagram.ui.follow

interface FollowInter {
    fun follow(id : Int)
    //fun checkFollowing(id: Int) : Response<Boolean>
    fun unfollow(id : Int)
}