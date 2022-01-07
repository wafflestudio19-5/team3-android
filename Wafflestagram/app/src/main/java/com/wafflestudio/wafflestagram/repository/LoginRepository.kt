package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.network.LoginService
import com.wafflestudio.wafflestagram.network.dto.LoginRequest
import com.wafflestudio.wafflestagram.network.dto.TokenResponse
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val loginService: LoginService){
    suspend fun getResponseByPing(): ResponseBody {
        return loginService.getResponseByPing()
    }

    suspend fun getResponseByLogin(loginRequest: LoginRequest): Response<ResponseBody> {
        return loginService.getResponseByLogin(loginRequest)
    }

    suspend fun getResponseBySocialLogin(provider: String): Response<ResponseBody> {
        return loginService.getResponseBySocialLogin(provider)
    }
}