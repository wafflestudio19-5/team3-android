package com.wafflestudio.wafflestagram.ui.follow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.databinding.ActivityFollowBinding
import retrofit2.Response

class FollowActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFollowBinding
    private val viewModel: FollowViewModel by viewModels()
    private lateinit var followAdapter: FollowAdapter
    private lateinit var followLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        followAdapter = FollowAdapter()
        followLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewFollow.apply {
            adapter = followAdapter
            layoutManager = followLayoutManager
        }

        val id = intent.getIntExtra("id", 0)
        val activity = intent.getIntExtra("activity", 0)

        viewModel.getMe()

        if(activity == 0){
            binding.textActivity.text = "팔로워"
            viewModel.getFollowerById(id, 0, 100)
        }else{
            binding.textActivity.text = "팔로잉"
            viewModel.getFollowingById(id, 0, 100)
        }

        viewModel.page.observe(this, {response ->
            if(response.isSuccessful){
                followAdapter.updateData(response.body()!!.content)
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