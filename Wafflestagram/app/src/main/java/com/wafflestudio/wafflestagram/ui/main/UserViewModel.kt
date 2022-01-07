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
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _feedList = MutableLiveData<List<Feed>>()
    val feedList: LiveData<List<Feed>> = _feedList

    private val _fetchUserInfo = MutableLiveData<Response<User>>()
    val fetchUserInfo: LiveData<Response<User>> = _fetchUserInfo

    private val _fetchFollower = MutableLiveData<Response<ResponseBody>>()
    val fetchFollower: LiveData<Response<ResponseBody>> = _fetchFollower

    private val _fetchFollowing = MutableLiveData<Response<ResponseBody>>()
    val fetchFollowing: LiveData<Response<ResponseBody>> = _fetchFollowing

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

    fun getMyFollower(){
        viewModelScope.launch {
            try {
                val data = userRepository.getMyFollower()
                _fetchFollower.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getMyFollowing(){
        viewModelScope.launch {
            try {
                val data = userRepository.getMyFollowing()
                _fetchFollowing.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    // user id로 다른 사람 정보 가져오기
    fun getInfoByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                val data = userRepository.getInfoByUserId(userId)
                _fetchUserInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFollowerByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                val data = userRepository.getFollowerByUserId(userId)
                _fetchFollower.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFollowingByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                val data = userRepository.getFollowingByUserId(userId)
                _fetchFollowing.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    // TODO: User 관련 정보 받아오기(ID, bio, posts, followers, followings, ...)
    // TODO(선택): followers, followings 누르면 나오는 유저 목록 불러오는 함수 구현
}