package com.wafflestudio.wafflestagram.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.wafflestudio.wafflestagram.databinding.ActivityDetailFeedBinding
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFeedActivity : AppCompatActivity() ,DetailFeedInterface {

    private lateinit var binding: ActivityDetailFeedBinding
    private val viewModel: DetailFeedViewModel by viewModels()
    private lateinit var feedAdapter: DetailFeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ZoomHelper.getInstance().maxScale = 3f

        feedAdapter = DetailFeedAdapter(this)
        feedLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recyclerViewFeed.apply {
            adapter = feedAdapter
            layoutManager = feedLayoutManager
        }

        val userId = intent.getIntExtra("userId", -1)
        val position = intent.getIntExtra("position" , -1)

        viewModel.getFeedsByUserId(userId, 0, 100)

        binding.recyclerViewFeed.scrollToPosition(position)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(
            Color.parseColor("#F6F6F6"),
            Color.parseColor("#D5D5D5"),
            Color.parseColor("#A6A6A6"),
            Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {
            viewModel.getFeedsByUserId(userId, 0, 100)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)
        }

        viewModel.page.observe(this, {response->
            if(response.isSuccessful){
                feedAdapter.updateData(response.body()!!.content)
            }else if(response.code() == 401){

            }else{

            }

        })

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    override fun like(id: Int, position: Int) {
        viewModel.like(id)
        feedAdapter.notifyItemChanged(position)
    }

    override fun unlike(id: Int, position: Int) {
        viewModel.unlike(id)
        feedAdapter.notifyItemChanged(position)
    }
}