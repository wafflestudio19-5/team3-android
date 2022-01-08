package com.wafflestudio.wafflestagram.ui.like

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.databinding.ActivityLikeBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response

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

        likeAdapter = LikeAdapter()
        likeLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewLike.apply {
            adapter = likeAdapter
            layoutManager = likeLayoutManager
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        val id = intent.getIntExtra("id", 0)

        viewModel.getMe()
        viewModel.getFeedById(id)

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                likeAdapter.updateData(response.body()!!.likes)
            }else if(response.code() == 401){
                // 토큰 삭제
            }else{

            }
        })
    }

    fun checkFollowing(id: Int) : Response<Boolean> {
        return viewModel.checkFollowing(id)
    }

    fun follow(id: Int){
        viewModel.follow(id)
    }

    fun unfollow(id: Int){
        viewModel.unfollow(id)
    }
}