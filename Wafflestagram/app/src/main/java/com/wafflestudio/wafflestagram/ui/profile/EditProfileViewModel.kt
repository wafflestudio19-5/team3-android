package com.wafflestudio.wafflestagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userService: UserService
): ViewModel(){

    private val _fetchUserInfoResponse = MutableLiveData<User>()
    var fetchUserInfoResponse: LiveData<User> = _fetchUserInfoResponse
}