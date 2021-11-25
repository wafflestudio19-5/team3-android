package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.network.LoginService
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val loginService: LoginService){
    suspend fun getResponseByLogin(): ResponseBody {
        return loginService.getResponseByLogin()
    }
}