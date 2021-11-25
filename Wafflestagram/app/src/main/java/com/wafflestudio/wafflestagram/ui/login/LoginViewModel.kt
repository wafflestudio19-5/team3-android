package com.wafflestudio.wafflestagram.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    var fetchResponse: String = ""

    fun getResponseByLogin() {
        viewModelScope.launch {
            try {
                val data = loginRepository.getResponseByLogin()
                fetchResponse = data.string()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}