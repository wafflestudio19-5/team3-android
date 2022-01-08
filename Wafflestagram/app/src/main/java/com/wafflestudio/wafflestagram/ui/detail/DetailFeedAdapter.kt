package com.wafflestudio.wafflestagram.ui.detail

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.wafflestudio.wafflestagram.databinding.ItemFeedBinding
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.ui.comment.CommentActivity
import com.wafflestudio.wafflestagram.ui.like.LikeActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import com.wafflestudio.wafflestagram.ui.main.ViewPagerImageAdapter
import java.time.format.DateTimeFormatter

class DetailFeedAdapter(val detailFeedInterface: DetailFeedInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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

        when(holder){
            is FeedViewHolder ->{
                holder.binding.apply {
                    buttonUsername.text = data.author?.username
                    val spannable = SpannableStringBuilder(data.author?.username)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(object : ClickableSpan(){
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                        override fun onClick(p0: View) {
                            val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                            intent.putExtra("id", data.author?.id!!.toInt())
                            ContextCompat.startActivity(holder.itemView.context, intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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

                    textDateCreated.text = data.createdAt!!.format(DateTimeFormatter.ofPattern( "MM월 dd일 HH시 mm분"))
                    textLike.text = "좋아요 " + data.likeSum + "개"

                    buttonComment.setOnClickListener {
                        val intent = Intent(holder.itemView.context, CommentActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    textLike.setOnClickListener {
                        val intent = Intent(holder.itemView.context, LikeActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    buttonLike.setOnClickListener {
                        if(buttonLike.isSelected){
                            buttonLike.isSelected = false
                            detailFeedInterface.unlike(data.id.toInt(), position)
                        }else{
                            buttonLike.isSelected = true
                            detailFeedInterface.like(data.id.toInt(), position)
                        }
                    }

                    buttonUserImage.setOnClickListener{
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author?.id!!.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    buttonUsername.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author?.id!!.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

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
        notifyDataSetChanged()
    }


    companion object{
        const val VIEW_TYPE_FEED = 0
    }
}