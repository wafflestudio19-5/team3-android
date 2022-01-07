package com.wafflestudio.wafflestagram.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemFeedGridBinding
import com.wafflestudio.wafflestagram.model.Feed

class DetailUserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var feeds : List<Feed> = listOf()

    inner class FeedViewHolder(val binding: ItemFeedGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFeedGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = feeds[position]
        if(holder is FeedViewHolder){
            holder.binding.apply {
                Glide.with(holder.itemView.context).load(data.photos.get(0).path).centerCrop().into(imagePhoto)
            }
        }
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    fun updateData(feeds : List<Feed>){
        this.feeds = feeds
        notifyDataSetChanged()
    }
}