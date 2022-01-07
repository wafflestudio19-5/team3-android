package com.wafflestudio.wafflestagram.ui.detail

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.wafflestudio.wafflestagram.databinding.ActivityDetailFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFeedBinding
    private val viewModel: DetailFeedViewModel by viewModels()
    private lateinit var feedAdapter: DetailFeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ZoomHelper.getInstance().maxScale = 3f

        feedAdapter = DetailFeedAdapter()
        feedLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recyclerViewFeed.apply {
            adapter = feedAdapter
            layoutManager = feedLayoutManager
        }

        val userId = intent.getIntExtra("userId", -1)
        val position = intent.getIntExtra("position" , -1)

        viewModel.getFeedsByUserId(userId, 0, 100)

        binding.recyclerViewFeed.scrollToPosition(position)

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
}