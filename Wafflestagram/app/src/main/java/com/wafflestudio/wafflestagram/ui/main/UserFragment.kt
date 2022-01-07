package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentUserBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.write.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment: Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserViewModel by activityViewModels()

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
            startActivity(Intent(_context, DetailFeedActivity::class.java).putExtra("id", it))
        }
        // recyclerView -> 3행 Grid 형식으로 지정
        userLayoutManager = GridLayoutManager(_context, 3)
        binding.recyclerViewPhotos.apply {
            adapter = userPhotoAdapter
            layoutManager = userLayoutManager
        }

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }


        
        // 데이터 저장
        viewModel.feedList.observe(viewLifecycleOwner, {
            userPhotoAdapter.updatePhotos(it, _context)
            binding.posts.text = userPhotoAdapter.itemCount.toString()
        })
    }
}