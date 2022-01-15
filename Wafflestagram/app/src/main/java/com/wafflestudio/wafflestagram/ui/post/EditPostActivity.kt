package com.wafflestudio.wafflestagram.ui.post

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ActivityEditPostBinding
import com.wafflestudio.wafflestagram.network.dto.EditPostRequest
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding
    private val viewModel: EditPostViewModel by viewModels()
    private lateinit var editPostAdapter: EditPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ZoomHelper.getInstance().maxScale = 3f

        editPostAdapter = EditPostAdapter()

        binding.viewPagerImage.apply {
            adapter = editPostAdapter
        }

        binding.indicatorImage.setViewPager(binding.viewPagerImage)

        val feedId = intent.getIntExtra("feedId", 0)
        val userId = intent.getIntExtra("userId", 0)
        val position = intent.getIntExtra("position", 0)

        viewModel.getFeedById(feedId)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonComplete.setOnClickListener {
            val request = EditPostRequest(binding.editContent.text.toString(), emptyList(), emptyList())
            viewModel.editPost(feedId, request)
        }

        viewModel.feedResponse.observe(this, {response->
            if(response.isSuccessful){
                val intent = Intent(this, DetailFeedActivity::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("position", position)
                startActivity(intent)
                finish()
            }
        })

        viewModel.fetchFeed.observe(this, {response->
            if(response.isSuccessful){
                val data = response.body()!!
                binding.buttonUsername.text = data.author!!.username
                Glide.with(this).load(data.author!!.profilePhotoURL).centerCrop().into(binding.buttonUserImage)
                binding.textCreated.text = getBetween(data.createdAt!!.plusHours(9), ZonedDateTime.now(
                    ZoneId.of("Asia/Seoul")))
                binding.editContent.setText(data.content)
                editPostAdapter.updateData(data.photos)
                if(editPostAdapter.itemCount == 1){
                    binding.indicatorImage.visibility = View.GONE
                }else{
                    binding.indicatorImage.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getBetween(time: LocalDateTime, time2: ZonedDateTime) : String{
        for(timeFormat in listOf(ChronoUnit.SECONDS, ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS)){
            val between = timeFormat.between(time, time2)
            when(timeFormat){
                ChronoUnit.SECONDS -> if(between < 60) return between.toString() + "초"
                ChronoUnit.MINUTES -> if(between < 60) return between.toString() + "분"
                ChronoUnit.HOURS -> if(between < 24) return between.toString() + "시간"
                ChronoUnit.DAYS -> if(between < 7) return between.toString() + "일"
                else -> {}
            }
        }
        return time.format(DateTimeFormatter.ofPattern( "MM월 dd일"))
    }
}