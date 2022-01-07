package com.wafflestudio.wafflestagram.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Photo
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _feedList = MutableLiveData<List<Feed>>()
    val feedList: LiveData<List<Feed>> = _feedList

    private val _fetchUserInfo = MutableLiveData<User>()
    val fetchUserInfo: LiveData<User> = _fetchUserInfo

    // 내 정보 가져오기
    fun getMyInfo(){
        viewModelScope.launch {
            try{
                val data = userRepository.getMyInfo()
                _fetchUserInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    // user id로 다른 사람 정보 가져오기
    fun getInfoByUserId(userId: Long){
        viewModelScope.launch {
            try {
                val data = userRepository.getInfoByUserId(userId)
                _fetchUserInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    // TODO: User 관련 정보 받아오기(ID, bio, posts, followers, followings, ...)
    // TODO(선택): followers, followings 누르면 나오는 유저 목록 불러오는 함수 구현
}