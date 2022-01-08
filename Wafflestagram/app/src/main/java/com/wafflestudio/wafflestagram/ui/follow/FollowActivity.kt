package com.wafflestudio.wafflestagram.ui.follow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.databinding.ActivityFollowBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowActivity : AppCompatActivity(), FollowInter {

    private lateinit var binding : ActivityFollowBinding
    private val viewModel: FollowViewModel by viewModels()
    private lateinit var followAdapter: FollowAdapter
    private lateinit var followLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        followAdapter = FollowAdapter(this)
        followLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewFollow.apply {
            adapter = followAdapter
            layoutManager = followLayoutManager
        }

        binding.buttonBack.setOnClickListener {
            finish()
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
            }else if(response.code() == 404){
                // 토큰 삭제
            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT)
            }
        })


    }

    /*fun checkFollowing(id: Int) : Response<Boolean> {
        return viewModel.checkFollowing(id)
    }*/

    override fun follow(id: Int){
        viewModel.follow(id)
    }

    override fun unfollow(id: Int){
        viewModel.unfollow(id)
    }
}