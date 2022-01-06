package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.network.UserService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService){

}