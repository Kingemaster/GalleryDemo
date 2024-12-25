package com.example.gallery.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gallery.NetWorkStatus
import com.example.gallery.R
import com.example.gallery.adapter.GalleryAdapter
import com.example.gallery.viewmodel.MyPagingViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {


    private val viewModel by activityViewModels<MyPagingViewModel>()
//    private val viewModel by viewModels<MyPagingViewModel>()
//    private val viewModel by lazy {
//        ViewModelProvider(requireActivity()).get(MyPagingViewModel::class.java)
//    }
    private val mAdapter : GalleryAdapter by lazy {
        GalleryAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        galleryRecyclerView.apply {
            adapter = mAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.pageList.observe(viewLifecycleOwner,Observer{
            mAdapter.submitList(it)
        })
        viewModel.netWorkStatus.observe(viewLifecycleOwner,Observer{
            Log.d("yxc","observe的结果是：$it")
            mAdapter.setNetWorkStatus(it)
            galleryRefreshLayout.isRefreshing = it == NetWorkStatus.INITIAL
        })

        galleryRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_menu,menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item_refresh->
                viewModel.refreshData()
        }
        return super.onOptionsItemSelected(item)
    }
}