package com.wafflestudio.wafflestagram.network

import okhttp3.ResponseBody
import retrofit2.http.GET

interface LoginService {

    @GET("/api/v1/ping/")
    suspend fun getResponseByLogin(): ResponseBody
}