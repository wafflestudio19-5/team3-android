package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentFeedBinding
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.write.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val viewModel: FeedViewModel by activityViewModels()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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

        val request = FeedPageRequest(feedAdapter.itemCount, 10)
        viewModel.getFeeds(request)

        viewModel.getMe()

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(Color.parseColor("#F6F6F6"),Color.parseColor("#D5D5D5"),Color.parseColor("#A6A6A6"),Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {
            feedAdapter.clearData()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)
            val request = FeedPageRequest(feedAdapter.itemCount, 10)
            viewModel.getFeeds(request)
        }

        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!binding.recyclerViewFeed.canScrollVertically(1)){
                    feedAdapter.deleteLoading()
                    val request = FeedPageRequest(feedAdapter.itemCount, 10)
                    viewModel.getFeeds(request)
                }
            }
        })

        viewModel.feedList.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                feedAdapter.addData(response.body()!!.toMutableList())
            }else if(response.code() == 401){
                // 토큰 만료시 토큰 삭제
                sharedPreferences.edit {
                    putString(TOKEN, "")
                }
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })

        viewModel.user.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                feedAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){

            }
        })
    }

    fun setRecyclerviewPosition(position: Int){
        binding.recyclerViewFeed.smoothScrollToPosition(position)
    }

    fun like(id:Int){
        viewModel.like(id)
    }

    fun unlike(id: Int){
        viewModel.unlike(id)
    }

    companion object{
        const val TOKEN = "token"
    }

}