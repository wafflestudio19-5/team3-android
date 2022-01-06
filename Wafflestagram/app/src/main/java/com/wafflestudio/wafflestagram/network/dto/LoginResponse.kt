package com.wafflestudio.wafflestagram.network.dto

data class LoginResponse(
    val status: Int,
    val token: String?,
    val message: String
)
