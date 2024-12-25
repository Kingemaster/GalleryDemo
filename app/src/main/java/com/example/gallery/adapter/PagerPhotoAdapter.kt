package com.example.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gallery.PhotoItem
import com.example.gallery.R
import kotlinx.android.synthetic.main.rcv_pager_photo_item.view.*

class PagerPhotoAdapter : ListAdapter<PhotoItem,PagerPhotoAdapter.MyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_pager_photo_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).fullViewUrl)
            .placeholder(R.drawable.ic_baseline_desktop_windows_24)
            .into(holder.itemView.imageView)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

private object DiffCallback : DiffUtil.ItemCallback<PhotoItem>(){

    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem) = oldItem .photoId == newItem.photoId

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem) = oldItem == newItem

}

