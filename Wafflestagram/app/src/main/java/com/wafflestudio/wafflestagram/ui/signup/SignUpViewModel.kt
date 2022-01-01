package com.wafflestudio.wafflestagram.ui.signup

import android.media.ToneGenerator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import com.wafflestudio.wafflestagram.network.dto.TokenResponse
import com.wafflestudio.wafflestagram.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
): ViewModel(){

    private val _tokenResponse = MutableLiveData<Response<TokenResponse>>()
    val tokenResponse : LiveData<Response<TokenResponse>> = _tokenResponse

    fun signUp(signUpRequest: SignUpRequest){
        viewModelScope.launch {
            try {
                val response = signUpRepository.signUp(signUpRequest)
                _tokenResponse.value = response
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

}