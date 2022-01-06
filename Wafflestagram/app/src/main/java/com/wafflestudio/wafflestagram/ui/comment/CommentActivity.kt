package com.wafflestudio.wafflestagram.ui.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wafflestudio.wafflestagram.databinding.ActivityCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}