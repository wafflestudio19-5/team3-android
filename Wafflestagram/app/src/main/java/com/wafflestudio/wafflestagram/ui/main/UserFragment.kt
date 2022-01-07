package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
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

    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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

        userPhotoAdapter = UserPhotoAdapter {
            startActivity(Intent(context, DetailFeedActivity::class.java).putExtra("id", it))
        }
        // recyclerView -> 3행 Grid 형식으로 지정
        userLayoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewPhotos.apply {
            adapter = userPhotoAdapter
            layoutManager = userLayoutManager
        }

        // post 작성 버튼
        binding.buttonAdd.setOnClickListener{
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }

        // profile 편집 버튼
        binding.buttonEditProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
            Timber.d("edit button was pushed")
        }

        // 더미 데이터 불러오기
        viewModel.loadData()
        
        // 데이터 저장
        viewModel.feedList.observe(viewLifecycleOwner, {
            userPhotoAdapter.updatePhotos(it, _context)
            binding.posts.text = userPhotoAdapter.itemCount.toString()
        })
        
        // TODO: user id도 sharedpreference에 담기
        // TODO: 자신인지 여부에 따라서 상단바 버튼 이미지, bio 아래 버튼 텍스트 바꾸기
    }
}