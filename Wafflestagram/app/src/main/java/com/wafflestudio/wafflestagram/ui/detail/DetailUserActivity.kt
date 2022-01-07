package com.wafflestudio.wafflestagram.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ActivityDetailUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val viewModel: DetailUserViewModel by viewModels()
    private lateinit var userAdapter: DetailUserAdapter
    private lateinit var userLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = DetailUserAdapter()
        userLayoutManager = GridLayoutManager(this, 3)

        binding.recyclerViewPhotos.apply {
            adapter = userAdapter
            layoutManager = userLayoutManager
        }

        val id = intent.getIntExtra("id", 0)

        viewModel.getFeedCount(id)
        viewModel.getFollowerCount(id)
        viewModel.getFollowingCount(id)
        viewModel.getInfoById(id)
        viewModel.getFeedsById(id, 0, 50)

        viewModel.page.observe(this, {response->
            if(response.isSuccessful){
                userAdapter.updateData(response.body()!!.content)
            }
        })

        viewModel.fetchUserInfo.observe(this, {response->
            if(response.isSuccessful){
                binding.buttonUsername.text = response.body()!!.username
                Glide.with(this).load(response.body()!!.profilePhotoURL).centerCrop().into(binding.userImage)
            }
        })

        viewModel.fetchFeedCount.observe(this,{response ->
            if(response.isSuccessful){
                binding.posts.text = response.body()!!.string()
            }
        })

        viewModel.fetchFollowerCount.observe(this,{response ->
            if(response.isSuccessful){
                binding.followers.text = response.body()!!.string()
            }
        })

        viewModel.fetchFollowingCount.observe(this,{response ->
            if(response.isSuccessful){
                binding.followings.text = response.body()!!.string()
            }
        })
    }
}