package com.example.gallery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.example.gallery.paging.PixabayDataSourceFactory

class MyPagingViewModel(application: Application):AndroidViewModel(application) {

    private val factory = PixabayDataSourceFactory()

    val currentPosition = MutableLiveData<Int>()
    val pageList = factory.toLiveData(1)
    val netWorkStatus = Transformations.switchMap(factory.dataSource) { it.netWorkStatus }

    fun refreshData(){
        pageList.value?.dataSource?.invalidate()
    }

    fun retry(){
        factory.dataSource.value?.retryFun?.invoke()
    }
}