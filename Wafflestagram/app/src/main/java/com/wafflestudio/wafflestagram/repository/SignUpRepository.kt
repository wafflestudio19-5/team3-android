package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.network.SignUpService
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import com.wafflestudio.wafflestagram.network.dto.TokenResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpRepository @Inject constructor(private val signUpService: SignUpService){
    suspend fun signUp(signUpRequest: SignUpRequest): Response<TokenResponse>{
        return signUpService.signUp(signUpRequest)
    }
}