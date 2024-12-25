package com.example.gallery.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallery.NetWorkStatus
import com.example.gallery.PhotoItem
import com.example.gallery.R
import com.example.gallery.viewmodel.MyPagingViewModel
import kotlinx.android.synthetic.main.rcv_gallery_footer_item.view.*
import kotlinx.android.synthetic.main.rcv_gallery_item.view.*

class GalleryAdapter(private val viewModel: MyPagingViewModel) : PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(ItemDiffCallback) {

    init {
        viewModel.retry()
    }

    private var hasFooter = false
    private var netWorkStatus:NetWorkStatus? = null

    private object ItemDiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.rcv_gallery_footer_item -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_gallery_footer_item,parent,false)
                (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                MyFooterHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_gallery_item,parent,false)
                MyPhotoHolder(view)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            R.layout.rcv_gallery_footer_item ->{
                (holder as MyFooterHolder).bindView(netWorkStatus)
            }
            else ->{
                val item = getItem(position)?:return
                (holder as MyPhotoHolder).bindView(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1) R.layout.rcv_gallery_footer_item
                else R.layout.rcv_gallery_item
    }

    fun setNetWorkStatus(status: NetWorkStatus?){
        this.netWorkStatus = status
        if (netWorkStatus == NetWorkStatus.INITIAL){
            //隐藏Footer
            if (hasFooter){
                notifyItemRemoved(itemCount - 1)
            }
            hasFooter = false
        }else{
            //显示Footer,分为2种情况
            //1 当前Footer正在显示内容，通知刷新一下
            //2 当前Footer没有显示，那就显示一下
            if (hasFooter) {
                notifyItemChanged(itemCount - 1)
            }else{
                hasFooter = true
                notifyItemInserted(itemCount - 1)
            }
        }
    }

    inner class MyPhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                viewModel.currentPosition.value = adapterPosition
                itemView.findNavController().navigate(R.id.action_galleryFragment_to_pagerFragment)
            }
        }

        fun bindView(item:PhotoItem){
            itemView.let {
                it.rcvItemPhotoIv.layoutParams.height = item.photoHeight
                it.rcvItemShimmerLayout.apply {
                    setShimmerColor(0x55FFFFFF)
                    setShimmerAngle(0)
                    startShimmerAnimation()
                    it.rcvItemUserIdTv.text = item.userId.toString()
                    it.rcvItemLikeTv.text = item.likes.toString()
                    it.rcvItemCollectionTv.text = item.collections.toString()
                }
            }

            Glide.with(itemView)
                .load(item.photoUrl)
                .placeholder(R.drawable.ic_baseline_desktop_windows_24)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemView.rcvItemShimmerLayout?.stopShimmerAnimation()
                        return false
                    }

                })
                .into(itemView.rcvItemPhotoIv)
        }

    }

    inner class MyFooterHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                viewModel.retry()
            }
        }

        fun bindView(status:NetWorkStatus?){
            with(itemView){
                Log.d("yxc","bindView:${status}")
                when(status){
                    NetWorkStatus.FAIL ->{
                        textView.text = "点击重试"
                        progressBar.visibility = View.GONE
                        isClickable = true
                    }
                    NetWorkStatus.COMPLETED ->{
                        textView.text = "暂无更多"
                        progressBar.visibility = View.GONE
                        isClickable = false
                    }
                    else ->{
                        textView.text = "正在加载"
                        progressBar.visibility = View.VISIBLE
                        isClickable = false
                    }
                }
            }
        }
    }
}


