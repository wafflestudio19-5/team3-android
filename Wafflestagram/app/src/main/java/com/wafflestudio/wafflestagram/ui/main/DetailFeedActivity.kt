package com.wafflestudio.wafflestagram.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

// Feed 하나만 조회할 때 실행되는 Activity(소원님께서 구현하실 예정)
@AndroidEntryPoint
class DetailFeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }
}