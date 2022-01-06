package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.network.LoginService
import com.wafflestudio.wafflestagram.network.dto.LoginResponse
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val loginService: LoginService){
    suspend fun getResponseByPing(): ResponseBody {
        return loginService.getResponseByPing()
    }

    suspend fun getResponseByLogin(username: String, password: String): LoginResponse {
        return loginService.getResponseByLogin(username, password)
    }
}