package com.wafflestudio.wafflestagram.ui.login

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.network.dto.LoginRequest
import com.wafflestudio.wafflestagram.network.dto.TokenResponse
import com.wafflestudio.wafflestagram.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    private val _fetchDummy = MutableLiveData<String>()
    var fetchDummy: LiveData<String> = _fetchDummy

    private val _fetchLoginResponse = MutableLiveData<Response<ResponseBody>>()
    var fetchLoginResponse: LiveData<Response<ResponseBody>> = _fetchLoginResponse

    private val _fetchSocialLoginUrl = MutableLiveData<Response<ResponseBody>>()
    var fetchSocialLoginUrl: LiveData<Response<ResponseBody>> = _fetchSocialLoginUrl

    fun getResponseByLogin(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                val data = loginRepository.getResponseByLogin(loginRequest)
                _fetchLoginResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getResponseBySocialLogin(provider: String) {
        viewModelScope.launch {
            try {
                val data = loginRepository.getResponseBySocialLogin(provider)
                _fetchSocialLoginUrl.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getResponseByGoogleLogin(idToken: String) {
        viewModelScope.launch {
            try {
                _fetchDummy.value = idToken
                /*
                val data = loginRepository.getResponseByGoogleLogin(idToken)
                _fetchLoginResponse.value = data
                 */
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getResponseByFacebookLogin(token: String){
        viewModelScope.launch {
            try {
                _fetchDummy.value = token
                /*
                val data = loginRepository.getResponseByFacebookLogin(token)
                _fetchLoginResponse.value = data
                 */
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}