package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentFeedBinding
import com.wafflestudio.wafflestagram.ui.write.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val viewModel: FeedViewModel by activityViewModels()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedAdapter = FeedAdapter()
        feedLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.recyclerViewFeed.apply {
            adapter = feedAdapter
            layoutManager = feedLayoutManager
        }

        binding.buttonLogo.setOnClickListener{
            binding.recyclerViewFeed.smoothScrollToPosition(0)
        }

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }

        //더미 데이터
        viewModel.loadData()

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(Color.parseColor("#F6F6F6"),Color.parseColor("#D5D5D5"),Color.parseColor("#A6A6A6"),Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {
            viewModel.loadData()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)

        }

        viewModel.feedList.observe(viewLifecycleOwner, {
            feedAdapter.updateData(it)
        })


    }

    fun setRecyclerviewPosition(position: Int){
        binding.recyclerViewFeed.smoothScrollToPosition(position)
    }

}