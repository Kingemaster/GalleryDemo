package com.example.gallery.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gallery.R
import com.example.gallery.adapter.PagerPhotoAdapter
import com.example.gallery.viewmodel.MyPagingViewModel
import com.example.gallery.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.fragment_pager.*
import kotlinx.android.synthetic.main.rcv_pager_photo_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE


class PagerFragment : Fragment() {

    private val viewModel: MyPagingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyPagingViewModel::class.java)
    }

    private val mAdapter: PagerPhotoAdapter by lazy {
        PagerPhotoAdapter()
    }

    private lateinit var launcher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        saveData()
                    }
                } else {
                    Toast.makeText(requireContext(), "获取存储权限失败，请打开权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager2.adapter = mAdapter
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pagerTagTv.text = "${position + 1} / ${viewModel.pageList.value?.size ?: 0} "
            }
        })

        viewModel.pageList.observe(viewLifecycleOwner,Observer{
            mAdapter.submitList(it)
            viewPager2.setCurrentItem((viewModel.currentPosition.value ?: 0), false)
            //这里可以设置滑动方向，默认是水平滑动
            //viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        })

        pagerSaveIv.setOnClickListener {
            launcher.launch(WRITE_PERMISSION)
        }
    }

    private suspend fun saveData() {
        withContext(Dispatchers.IO) {
            val holder =
                (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoAdapter.MyViewHolder
            val bitmap = holder.itemView.imageView.drawable.toBitmap()
            val saveUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            ) ?: kotlin.run {
                Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)) {
                    MainScope().launch {
                        Toast.makeText(requireContext(), "存储成功", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    MainScope().launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}