package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ActivityDetailFeedBinding
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailFeedActivity : AppCompatActivity() ,DetailFeedInterface {

    private lateinit var binding: ActivityDetailFeedBinding
    private val viewModel: DetailFeedViewModel by viewModels()
    private lateinit var feedAdapter: DetailFeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager
    private var isFirstCall: Boolean = true
    private var itemPosition : Int = -1
    private var pageNumber: Int = 0
    private var totalPage: Int = 1
    private var isLast: Boolean = false

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
        val position = intent.getIntExtra("position" , 0)

        viewModel.getMe()
        viewModel.getFeedsByUserId(userId, 0, 100)


        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(
            Color.parseColor("#F6F6F6"),
            Color.parseColor("#D5D5D5"),
            Color.parseColor("#A6A6A6"),
            Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {
            feedAdapter.clearData()
            viewModel.getFeedsByUserId(userId, 0, 100)
            isFirstCall = false
            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)
        }

        viewModel.page.observe(this, {response->
            if(response.isSuccessful){
                feedAdapter.addData(response.body()!!.content.toMutableList())
                if(isFirstCall){
                    binding.recyclerViewFeed.scrollToPosition(position)
                }
                //feedAdapter.updateData(response.body()!!.content[0].author)
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit {
                    putString(FeedFragment.TOKEN, "")
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else{

            }
        })

        viewModel.user.observe(this, {response->
            if(response.isSuccessful){
                feedAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){

            }
        })

        viewModel.likeResponse.observe(this, {response ->
            if(response.isSuccessful){
                //feedAdapter.changeData(response.body()!!, itemPosition)
            }
        })

    }

    override fun deleteFeed(id: Int, position: Int){
        viewModel.deleteFeed(id)
        feedAdapter.notifyItemRemoved(position)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    override fun like(id: Int, position: Int) {
        viewModel.like(id)
        itemPosition = position
    }

    override fun unlike(id: Int, position: Int) {
        viewModel.unlike(id)
        itemPosition = position
    }
}