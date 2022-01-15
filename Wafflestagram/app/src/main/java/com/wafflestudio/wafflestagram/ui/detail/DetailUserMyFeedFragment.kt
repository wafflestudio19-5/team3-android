package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentDetailUserMyFeedBinding
import com.wafflestudio.wafflestagram.databinding.FragmentUserMyFeedBinding
import com.wafflestudio.wafflestagram.ui.main.UserFragment
import com.wafflestudio.wafflestagram.ui.main.UserPhotoAdapter
import com.wafflestudio.wafflestagram.ui.main.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserMyFeedFragment: Fragment() {

    private lateinit var binding: FragmentDetailUserMyFeedBinding
    private val viewModel: DetailFeedViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userAdapter: DetailUserAdapter
    private lateinit var userLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailUserMyFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val id = intent.getIntExtra("id", -1)
        val id = arguments!!.getInt("id", -1)

        // Setting Adapter and LayoutManager
        userAdapter = DetailUserAdapter{startActivity(
            Intent(activity, DetailFeedActivity::class.java)
                .putExtra("position", it)
                .putExtra("userId", id)
        )}
        userLayoutManager = GridLayoutManager(context, 3)

        binding.recyclerViewPhotos.apply {
            adapter = userAdapter
            layoutManager = userLayoutManager
        }

        viewModel.getFeedsByUserId(id, 0, 50)

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                userAdapter.updateData(response.body()!!.content)
            }
        })
    }
}