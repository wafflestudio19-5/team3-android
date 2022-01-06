package com.wafflestudio.wafflestagram.ui.login

import android.content.SharedPreferences
import android.provider.Settings.Global.putString
import android.text.Editable
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.network.dto.LoginResponse
import com.wafflestudio.wafflestagram.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    @Inject
    private lateinit var sharedPreferences: SharedPreferences

    private val _fetchPingResponse = MutableLiveData<String>()
    var fetchPingResponse: LiveData<String> = _fetchPingResponse

    private val _fetchLoginResponse = MutableLiveData<LoginResponse>()
    var fetchLoginResponse: LiveData<LoginResponse> = _fetchLoginResponse

    fun getResponseByPing(){
        viewModelScope.launch {
            try{
                val data = loginRepository.getResponseByPing()
                _fetchPingResponse.value = data.string()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getResponseByLogin(username: String, password: String) {
        viewModelScope.launch {
            try {
                val data = loginRepository.getResponseByLogin(username, password)
                if(data.message == "success"){
                    sharedPreferences.edit {
                        putString("token", data.token)
                    }
                }
                else {
                    if(data.message != null){
                        val error = data.message
                    }
                }
                _fetchLoginResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}