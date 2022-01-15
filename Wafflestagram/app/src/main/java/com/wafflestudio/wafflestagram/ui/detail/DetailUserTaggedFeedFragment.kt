package com.wafflestudio.wafflestagram.ui.detail

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentDetailUserTaggedFeedBinding
import com.wafflestudio.wafflestagram.ui.main.UserPhotoAdapter
import com.wafflestudio.wafflestagram.ui.main.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserTaggedFeedFragment: Fragment() {

    private lateinit var binding: FragmentDetailUserTaggedFeedBinding
    private val viewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1 // -1: default value

    private lateinit var userPhotoAdapter: UserPhotoAdapter
    private lateinit var userLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailUserTaggedFeedBinding.inflate(inflater, container, false)
        return binding.root
    }
}