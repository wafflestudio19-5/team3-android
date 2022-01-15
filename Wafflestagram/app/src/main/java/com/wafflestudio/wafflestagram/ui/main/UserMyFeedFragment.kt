package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentUserMyFeedBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UserMyFeedFragment: Fragment() {

    private lateinit var binding: FragmentUserMyFeedBinding
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
        binding = FragmentUserMyFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = sharedPreferences.getInt(UserFragment.CURRENT_USER_ID, -1)
        Timber.d(currentUserId.toString())

        // Setting Adapter and LayoutManager
        userPhotoAdapter = UserPhotoAdapter {
            startActivity(
                Intent(context, DetailFeedActivity::class.java)
                    .putExtra("position", it)
                    .putExtra("userId", currentUserId)
            )
        }
        // recyclerView -> 3행 Grid 형식으로 지정
        userLayoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewPhotos.apply {
            adapter = userPhotoAdapter
            layoutManager = userLayoutManager
        }
        viewModel.getMyFeeds(0, 100)

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                userPhotoAdapter.updatePhotos(response.body()!!.content, context!!)
            }else{

            }

        })
    }
}