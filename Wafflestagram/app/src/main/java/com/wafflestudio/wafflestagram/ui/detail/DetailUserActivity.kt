package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityDetailUserBinding
import com.wafflestudio.wafflestagram.ui.follow.FollowActivity
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val viewModel: DetailUserViewModel by viewModels()
    /*
    private lateinit var userAdapter: DetailUserAdapter
    private lateinit var userLayoutManager: GridLayoutManager
     */

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)

        val viewpagerUserFragmentAdapter = ViewPagerDetailUserAdapter(this)
        viewpagerUserFragmentAdapter.setId(id)
        binding.viewPager.adapter = viewpagerUserFragmentAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> Unit }.attach()

        binding.tabLayout.getTabAt(0)!!.setIcon(R.drawable.icon_grid)
        binding.tabLayout.getTabAt(1)!!.setIcon(R.drawable.icon_tagged)

        /*
        userAdapter = DetailUserAdapter{startActivity(
            Intent(this, DetailFeedActivity::class.java)
                .putExtra("position", it)
                .putExtra("userId", id)
        )}
        userLayoutManager = GridLayoutManager(this, 3)

        binding.recyclerViewPhotos.apply {
            adapter = userAdapter
            layoutManager = userLayoutManager
        }
         */

        viewModel.getMyInfo()
        viewModel.getInfoById(id)
        viewModel.getFeedCount(id)
        viewModel.getFollowerCount(id)
        viewModel.getFollowingCount(id)
        viewModel.getFeedsById(id, 0, 50)
        viewModel.checkFollowing(id)

        /*
        viewModel.page.observe(this, {response->
            if(response.isSuccessful){
                userAdapter.updateData(response.body()!!.content)
            }
        })
         */

        viewModel.fetchUserInfo.observe(this, {response->
            if(response.isSuccessful){
                val data = response.body()!!
                binding.buttonUsername.text = data.username
                if(data.bio.isNullOrBlank()){
                    binding.textBio.visibility = View.GONE
                }else{
                    binding.textBio.text = data.bio
                    binding.textBio.visibility = View.VISIBLE
                }
                if(data.website.isNullOrBlank()){
                    binding.textWebsite.visibility = View.GONE
                }else{
                    binding.textWebsite.visibility = View.VISIBLE
                    binding.textWebsite.text = data.website
                }
                if(data.name.isNullOrBlank()){
                    binding.textName.visibility = View.GONE
                }else{
                    binding.textName.visibility = View.VISIBLE
                    binding.textName.text = data.name
                }
                Glide.with(this).load(response.body()!!.profilePhotoURL).centerCrop().into(binding.userImage)
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit {
                    putString(FeedFragment.TOKEN, "")
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
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

        viewModel.checkFollowing.observe(this, {response->
            if(response.isSuccessful){
                if(response.body()!!.string() == "true"){
                    binding.buttonFollow.isSelected = true
                    binding.buttonFollow.setTextColor(Color.parseColor("#FF000000"))
                    binding.buttonFollow.text = "팔로잉"
                }else{
                    binding.buttonFollow.isSelected = false
                    binding.buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                    binding.buttonFollow.text = "팔로우"
                }
            }
        })

        viewModel.fetchMyInfo.observe(this, {response ->
            if(response.isSuccessful){
                if(response.body()!!.id.toInt() == id){
                    binding.layoutFollowMessage.visibility = View.GONE
                }
            }
        })

        binding.buttonFollow.setOnClickListener {
            if(binding.buttonFollow.isSelected){
                binding.buttonFollow.isSelected = false
                binding.buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.buttonFollow.text = "팔로우"
                viewModel.unfollow(id)
            }else{
                binding.buttonFollow.isSelected = true
                binding.buttonFollow.setTextColor(Color.parseColor("#FF000000"))
                binding.buttonFollow.text = "팔로잉"
                viewModel.follow(id)
            }
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.areaFollowers.setOnClickListener {
            val intent = Intent(this, FollowActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("activity", 0)
            startActivity(intent)
        }

        binding.areaFollowings.setOnClickListener {
            val intent = Intent(this, FollowActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("activity", 1)
            startActivity(intent)
        }

        binding.areaPosts.setOnClickListener {
            val intent = Intent(this, DetailFeedActivity::class.java)
            intent.putExtra("userId", id)
            startActivity(intent)
        }


    }
}