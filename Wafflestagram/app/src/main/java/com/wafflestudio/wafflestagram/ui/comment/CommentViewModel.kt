package com.wafflestudio.wafflestagram.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository
): ViewModel() {

    private val _feed = MutableLiveData<Response<Feed>>()
    val feed: LiveData<Response<Feed>> = _feed

    private val _comment = MutableLiveData<Response<Comment>>()
    val comment: LiveData<Response<Comment>> = _comment


    fun getFeedById(id: Int){
        viewModelScope.launch {
            try {
                val data = commentRepository.getFeedById(id)
                _feed.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun addComment(id: Int, addCommentRequest: AddCommentRequest){
        viewModelScope.launch {
            try {
                val data = commentRepository.addComment(id, addCommentRequest)
                _comment.value = data
            }catch (e : IOException){
                Timber.e(e)
            }
        }
    }
}