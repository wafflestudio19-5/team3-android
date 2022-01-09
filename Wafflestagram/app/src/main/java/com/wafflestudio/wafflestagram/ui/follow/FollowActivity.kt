package com.wafflestudio.wafflestagram.ui.follow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.databinding.ActivityFollowBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowActivity : AppCompatActivity(), FollowInter {

    private lateinit var binding : ActivityFollowBinding
    private val viewModel: FollowViewModel by viewModels()
    private lateinit var followAdapter: FollowAdapter
    private lateinit var followLayoutManager: LinearLayoutManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
        }else{
            binding.textActivity.text = "팔로잉"
        }

        viewModel.page.observe(this, {response ->
            if(response.isSuccessful){
                followAdapter.updateData(response.body()!!.content)
            }else if(response.code() == 401){
                sharedPreferences.edit {
                    putString(FeedFragment.TOKEN, "")
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT)
            }
        })

        viewModel.followPage.observe(this,{response->
            if(response.isSuccessful){
                followAdapter.updateFollowing(response.body()!!.content)
                if(activity == 0){
                    viewModel.getFollowerById(id, 0, 100)
                }else{
                    viewModel.getFollowingById(id, 0, 100)
                }
            }
        })

        viewModel.user.observe(this, {reponse ->
            if(reponse.isSuccessful){
                followAdapter.updateUser(reponse.body()!!)
                viewModel.getMyFollowing(0, 500)
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