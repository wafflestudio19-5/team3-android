package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.network.dto.LoginRequest
import com.wafflestudio.wafflestagram.network.dto.TokenResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface LoginService {
    @POST("/api/v1/users/signin/")
    suspend fun getResponseByLogin(
        @Body loginRequest: LoginRequest
    ): Response<ResponseBody>

    @GET("/api/v1/social_login/{social_login_type}/")
    suspend fun getResponseBySocialLogin(
        @Path("social_login_type") provider: String
    ): Response<ResponseBody>

    @POST("/api/v1/social_login/google/")
    suspend fun getResponseByGoogleLogin(
        @Header("idToken") idToken: String
    ): Response<ResponseBody>

    @POST("/api/v1/social_login/facebook/")
    suspend fun getResponseByFacebookLogin(
        @Header("idToken") token: String
    ): Response<ResponseBody>
}