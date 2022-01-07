package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentUserBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.follow.FollowActivity
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.profile.EditProfileActivity
import com.wafflestudio.wafflestagram.ui.write.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment: Fragment() {
    // Assume that "username" is passed using putExtra() method
    // which contains username of user whose profile is showed
    // and we have "currentUsername" as sharedPreference

    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1 // -1: default value

    private lateinit var userPhotoAdapter: UserPhotoAdapter
    private lateinit var userLayoutManager: GridLayoutManager
    private lateinit var _context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _context = container!!.context
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // change UI and get info according to user ID
        currentUserId = sharedPreferences.getInt(CURRENT_USER_ID, -1)
        val userId = arguments?.getInt(USER_ID, -1)
        if(userId == currentUserId){
            // the user is me
            viewModel.getMyInfo()
            viewModel.getMyFollowerCount()
            viewModel.getMyFollowingCount()
            viewModel.getMyFeedCount()

            binding.buttonBack.visibility = View.GONE
            binding.buttonDM.visibility = View.GONE
            binding.buttonMenu.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_close))

            // post 작성 버튼
            binding.buttonAdd.setOnClickListener{
                val intent = Intent(context, AddPostActivity::class.java)
                startActivity(intent)
            }

            // profile 편집 버튼
            binding.buttonEditProfile.setOnClickListener {
                val intent = Intent(context, EditProfileActivity::class.java)
                val userInfo = viewModel.fetchUserInfo.value!!.body()
                intent.putExtra("name", userInfo!!.name)
                    .putExtra("username", userInfo.username)
                    .putExtra("website", userInfo.website)
                    .putExtra("bio", userInfo.bio)
                startActivity(intent)
            }

            binding.buttonMenu.setOnClickListener {
                sharedPreferences.edit {
                    putString("token", "")
                }
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            // the user is other one
            viewModel.getInfoByUserId(userId!!)
            viewModel.getFollowerCountByUserId(userId)
            viewModel.getFollowingCountByUserId(userId)
            viewModel.getFeedCountByUserId(userId)

            binding.buttonAdd.visibility = View.GONE
            binding.buttonMenu.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_more))
            checkFollowingStatus()
        }
        userPhotoAdapter = UserPhotoAdapter {
            startActivity(
                Intent(context, DetailFeedActivity::class.java)
                .putExtra("position", it)
                .putExtra("userId", userId)
            )
        }
        // recyclerView -> 3행 Grid 형식으로 지정
        userLayoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewPhotos.apply {
            adapter = userPhotoAdapter
            layoutManager = userLayoutManager
        }
        viewModel.getMyFeeds(0, 100)
        
        // 데이터 저장

        viewModel.fetchFollowingCount.observe(viewLifecycleOwner, { response ->
                val data = response.body()!!
                if(response.isSuccessful){
                    binding.followings.text = data.string()
                } else {
                    Toast.makeText(context, "팔로잉 정보를 불러 올 수 없습니다", Toast.LENGTH_SHORT).show()
                }
        })

        viewModel.fetchFollowerCount.observe(viewLifecycleOwner, { response ->
            val data = response.body()!!
            if(response.isSuccessful){
                binding.followers.text = data.string()
            } else {
                Toast.makeText(context, "팔로워 정보를 불러 올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.fetchFeedCount.observe(viewLifecycleOwner, { response ->
            val data = response.body()!!
            if(response.isSuccessful){
                binding.feeds.text = data.string()
            } else {
                Toast.makeText(context, "피드 정보를 불러 올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                userPhotoAdapter.updatePhotos(response.body()!!.content, context!!)
            }else{

            }

        })

        viewModel.fetchUserInfo.observe(viewLifecycleOwner, { response ->
            if(response.isSuccessful){
                val data = response.body()!!
                Glide.with(_context)
                    .load(data.profilePhotoURL)
                    .into(binding.userImage)
                binding.buttonUsername.text = data.username
                binding.textBio.text = data.bio
            } else {
                Toast.makeText(context, "유저 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        })

        binding.areaFollowers.setOnClickListener {
            val intent = Intent(context, FollowActivity::class.java)
            intent.putExtra("id", currentUserId)
            intent.putExtra("activity", 0)
            startActivity(intent)
        }

        binding.areaFollowings.setOnClickListener {
            val intent = Intent(context, FollowActivity::class.java)
            intent.putExtra("id", currentUserId)
            intent.putExtra("activity", 1)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.menu_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings ->
                Toast.makeText(context, "settings select", Toast.LENGTH_SHORT).show()

            R.id.menu_logout ->
                Toast.makeText(context, "logout select", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkFollowingStatus() {
        // check whether this user is followed by me
        // and set text on buttonEditProfile according to result
        binding.buttonEditProfile.text = "팔로우"
    }

    companion object {
        const val CURRENT_USER_ID = "currentUserId"
        const val USER_ID = "userId"
    }
}