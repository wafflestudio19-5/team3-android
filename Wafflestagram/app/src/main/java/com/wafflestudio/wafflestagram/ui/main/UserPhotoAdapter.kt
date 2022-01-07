package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemUserPhotoBinding
import com.wafflestudio.wafflestagram.model.Feed
import timber.log.Timber

// OnSelectClickListener의 param: feed의 id
class UserPhotoAdapter(private val onSelectClickListener: (Int) -> (Unit)) : RecyclerView.Adapter<UserPhotoAdapter.UserPhotoViewHolder>() {

    private var photos: List<Feed> = listOf()
    private lateinit var context: Context

    inner class UserPhotoViewHolder(val binding: ItemUserPhotoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPhotoViewHolder {
        val binding = ItemUserPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserPhotoViewHolder, position: Int) {
        val data = photos[position]

        // 각 feed의 첫 사진 띄우기
        try {
            Glide.with(context)
                .load(data.photos[0].path)
                .into(holder.binding.userPhoto)
        } catch(e: Throwable) {
            Timber.e(e)
        }
        // 사진 클릭 시 해당하는 feed로 이동
        holder.itemView.setOnClickListener{
            onSelectClickListener(data.id)
        }

    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun updatePhotos(photos: List<Feed>, context: Context){
        this.photos = photos
        this.context = context
        this.notifyDataSetChanged()
    }
}

// TODO: parameter로 clicklistener 함수 넘겨서 사진 클릭 시 해당 id의 feed로 이동하도록!