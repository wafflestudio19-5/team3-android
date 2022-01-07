package com.wafflestudio.wafflestagram.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.repository.DetailFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailFeedViewModel @Inject constructor(
    private val detailFeedRepository: DetailFeedRepository
): ViewModel(){

    private val _page = MutableLiveData<Response<Page>>()
    val page: LiveData<Response<Page>> = _page

    fun getFeedsByUserId(id: Int, offset: Int, limit : Int){
        viewModelScope.launch {
            try {
                val data = detailFeedRepository.getFeedsByUserId(id, offset, limit)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}