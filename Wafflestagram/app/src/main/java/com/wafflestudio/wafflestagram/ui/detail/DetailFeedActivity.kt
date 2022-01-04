package com.wafflestudio.wafflestagram.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wafflestudio.wafflestagram.databinding.ActivityDetailFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}