package com.wafflestudio.wafflestagram.ui.main

import androidx.lifecycle.ViewModel
import com.wafflestudio.wafflestagram.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel(){
    // TODO: Implement the ViewModel
}