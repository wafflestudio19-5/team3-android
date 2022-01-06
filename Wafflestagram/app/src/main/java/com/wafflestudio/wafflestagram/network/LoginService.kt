package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.network.dto.LoginResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {

    @GET("/api/v1/ping")
    suspend fun getResponseByPing(): ResponseBody

    @POST("/api/v1/users/signin/")
    suspend fun getResponseByLogin(username: String, password: String): LoginResponse
}