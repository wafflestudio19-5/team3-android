package com.wafflestudio.wafflestagram.ui.like

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.wafflestudio.wafflestagram.databinding.ActivityLikeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}