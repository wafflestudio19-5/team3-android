package com.wafflestudio.wafflestagram.ui.write

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.PagerItemImageBinding

class ImageDialogAdapter(): RecyclerView.Adapter<ImageDialogAdapter.ImageViewHolder>() {

    private var imageUris : List<Uri> = listOf()

    inner class ImageViewHolder(val binding: PagerItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = PagerItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val data = imageUris[position]
        holder.binding.imagePhoto.setImageURI(data)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    fun updateData(imageUris : List<Uri>){
        this.imageUris = imageUris
        notifyDataSetChanged()
    }


}