package com.wafflestudio.wafflestagram.ui.main

import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.wafflestudio.wafflestagram.databinding.ItemFeedBinding
import com.wafflestudio.wafflestagram.model.Feed
import java.time.Duration
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class FeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var feeds: List<Feed> = listOf()

    inner class FeedViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root){
        fun onViewAppear(){
            binding.numberIndicatorPager.alpha = 1.0f

            fadeAwayIndicatorWithDelay(2000, 2000)
        }

        private fun fadeAwayIndicatorWithDelay(fadeDuration: Long, delay: Long){
            ObjectAnimator.ofFloat(binding.numberIndicatorPager, "alpha", 1f, 0f).apply {
                duration = fadeDuration
                startDelay = delay
            }.start()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = feeds[position]

        var imageAdapter : ViewPagerImageAdapter = ViewPagerImageAdapter()
        var imageLayoutManager : LinearLayoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)

        when(holder){
            is FeedViewHolder ->{
                holder.binding.apply {
                    buttonUsername.text = data.writer
                    val spannable = SpannableStringBuilder(data.writer)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.content)
                    textContent.text = spannable
                    viewPagerImage.apply {
                        adapter = imageAdapter
                        imageAdapter.updateData(data.photos)
                    }
                    if(imageAdapter.itemCount == 1){
                        indicatorImage.visibility = View.GONE
                        numberIndicatorPager.visibility = View.GONE
                    }else{
                        indicatorImage.visibility = View.VISIBLE
                    }
                    indicatorImage.setViewPager(viewPagerImage)
                    textTotal.text = imageAdapter.itemCount.toString()
                    viewPagerImage.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback(){
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            textCurrent.text = (position + 1).toString()
                            holder.onViewAppear()
                        }
                    })

                }

            }
        }


    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        when(holder){
            is FeedViewHolder ->{
                holder.onViewAppear()
            }
        }
        super.onViewAttachedToWindow(holder)
    }

    fun updateData(feeds : List<Feed>){
        this.feeds = feeds
    }
}