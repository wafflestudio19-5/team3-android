package com.wafflestudio.wafflestagram.network.dto

data class SignUpRequest(
    val email:String,
    val password: String,
    val name: String,
    val username: String,
    val birthday: String,
    val phoneNumber: String
)
