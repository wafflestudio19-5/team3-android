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

    private val _fetchFollowerCount = MutableLiveData<Response<ResponseBody>>()
    val fetchFollowerCount: LiveData<Response<ResponseBody>> = _fetchFollowerCount

    private val _fetchFollowingCount = MutableLiveData<Response<ResponseBody>>()
    val fetchFollowingCount: LiveData<Response<ResponseBody>> = _fetchFollowingCount

    private val _fetchFeedCount = MutableLiveData<Response<ResponseBody>>()
    val fetchFeedCount: LiveData<Response<ResponseBody>> = _fetchFeedCount

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

    fun getMyFollowerCount(){
        viewModelScope.launch {
            try {
                val data = userRepository.getMyFollower()
                _fetchFollowerCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getMyFollowingCount(){
        viewModelScope.launch {
            try {
                val data = userRepository.getMyFollowing()
                _fetchFollowingCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getMyFeedCount(){
        viewModelScope.launch {
            try {
                val data = userRepository.getMyFeedCount()
                _fetchFeedCount.value = data
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

    fun getFollowerCountByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                val data = userRepository.getFollowerByUserId(userId)
                _fetchFollowerCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFollowingCountByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                val data = userRepository.getFollowingByUserId(userId)
                _fetchFollowingCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFeedCountByUserId(userId: Int){
        viewModelScope.launch {
            try {
                val data = userRepository.getFeedCountByUserId(userId)
                _fetchFeedCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    // TODO: User 관련 정보 받아오기(ID, bio, posts, followers, followings, ...)
    // TODO(선택): followers, followings 누르면 나오는 유저 목록 불러오는 함수 구현
}