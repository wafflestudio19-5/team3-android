package com.wafflestudio.wafflestagram.ui.like

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.databinding.ActivityLikeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikeBinding
    private val viewModel: LikeViewModel by viewModels()
    private lateinit var likeAdapter: LikeAdapter
    private lateinit var likeLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}