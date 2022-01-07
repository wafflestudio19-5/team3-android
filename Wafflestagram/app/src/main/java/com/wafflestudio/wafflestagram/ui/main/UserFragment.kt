package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentUserBinding
import com.wafflestudio.wafflestagram.databinding.ItemUserPhotoBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.profile.EditProfileActivity
import com.wafflestudio.wafflestagram.ui.write.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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
    private var currentUserId: Long = -1 // -1: default value

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
        currentUserId = sharedPreferences.getLong(CURRENT_USER_ID, -1)
        val userId = arguments!!.getLong(USER_ID, -1)
        if(userId == currentUserId){
            // the user is me
            viewModel.getMyInfo()
            binding.buttonBack.visibility = View.GONE
            binding.buttonDM.visibility = View.GONE

            // post 작성 버튼
            binding.buttonAdd.setOnClickListener{
                val intent = Intent(context, AddPostActivity::class.java)
                startActivity(intent)
            }

            // profile 편집 버튼
            binding.buttonEditProfile.setOnClickListener {
                val intent = Intent(context, EditProfileActivity::class.java)
                // TODO: put Extra data using viewModel variables
                startActivity(intent)
            }
        } else {
            // the user is other one
            viewModel.getInfoByUserId(userId)
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
        
        // 데이터 저장
        viewModel.feedList.observe(viewLifecycleOwner, {
            userPhotoAdapter.updatePhotos(it, _context)
            // save post count
            binding.posts.text = userPhotoAdapter.itemCount.toString()
            // TODO: save Followers, Followings count
        })
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