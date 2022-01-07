package com.wafflestudio.wafflestagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import com.wafflestudio.wafflestagram.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){

    private val _fetchUserInfoResponse = MutableLiveData<Response<ResponseBody>>()
    var fetchUserInfoResponse: LiveData<Response<ResponseBody>> = _fetchUserInfoResponse

    fun updateUser(updateUserRequest: UpdateUserRequest){
        viewModelScope.launch {
            try {
                val data = userRepository.updateUser(updateUserRequest)
                _fetchUserInfoResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}