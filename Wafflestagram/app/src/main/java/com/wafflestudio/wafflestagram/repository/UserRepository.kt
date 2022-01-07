package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService){
    suspend fun getMyInfo(): User {
        return userService.getMyInfo()
    }

    suspend fun getInfoByUsername(username: String): User {
        return userService.getInfoByUsername(username)
    }
}