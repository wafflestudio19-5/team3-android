package com.wafflestudio.wafflestagram.ui.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.repository.LikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val likeRepository: LikeRepository
) : ViewModel(){

    private val _feed = MutableLiveData<Response<Feed>>()
    val feed: LiveData<Response<Feed>> = _feed

    fun getFeedById(id: Int){
        viewModelScope.launch {
            try {
                val data = likeRepository.getFeedById(id)
                _feed.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}