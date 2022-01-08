package com.wafflestudio.wafflestagram.ui.comment

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemCommentBinding
import com.wafflestudio.wafflestagram.databinding.ItemContentBinding
import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import java.time.format.DateTimeFormatter

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var comments: List<Comment> = listOf()
    private var feed: Feed? = null

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ContentViewHolder(val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_CONTENT ->{
                val binding = ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ContentViewHolder(binding)
            }

            VIEW_TYPE_COMMENT ->{
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CommentViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0){
            val data = feed!!
            if(holder is ContentViewHolder){
                holder.binding.apply {
                    val spannable = SpannableStringBuilder(data.author?.username)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(object : ClickableSpan(){
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                        override fun onClick(p0: View) {
                            // user page
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.content)
                    textContent.text = spannable

                    //textTime.text = data.createdAt!!.format(DateTimeFormatter.ofPattern( "MM월 dd일 HH시 mm분"))

                    //프로필 사진
                    Glide.with(holder.itemView.context).load(data.author?.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)
                }
            }
        }else{
            val data = comments[position-1]
            if(holder is CommentViewHolder){
                holder.binding.apply {
                    textContent.text = data.text
                    //textReplyNumber.text = data.replies.size.toString()
                }
                Glide.with(holder.itemView.context).load(data.writer.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)
            }
        }



    }

    override fun getItemCount(): Int {
        return if(feed == null){
            0
        }else{
            comments.size + 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            VIEW_TYPE_CONTENT
        }else{
            VIEW_TYPE_COMMENT
        }
    }

    fun updateData(feed: Feed){
        this.feed = feed
        this.comments = feed.comments
        this.notifyDataSetChanged()
    }

     companion object{
         const val VIEW_TYPE_CONTENT = 0
         const val VIEW_TYPE_COMMENT = 1
     }
}