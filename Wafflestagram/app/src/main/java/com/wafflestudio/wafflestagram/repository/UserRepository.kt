package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService){
    suspend fun getMyInfo(): Response<User> {
        return userService.getMyInfo()
    }

    suspend fun getInfoByUserId(userId: Long): Response<User> {
        return userService.getInfoByUserId(userId)
    }

    fun updateUser(updateUserRequest: UpdateUserRequest): Response<ResponseBody> {
        return userService.updateUser(updateUserRequest)
    }
}