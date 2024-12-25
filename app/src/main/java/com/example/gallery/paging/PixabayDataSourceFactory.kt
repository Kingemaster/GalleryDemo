package com.example.gallery.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.gallery.PhotoItem

class PixabayDataSourceFactory : DataSource.Factory<Int,PhotoItem>() {
    val dataSource = MutableLiveData<PixabayDataSource>()
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource().apply {
            dataSource.postValue(this)
        }
    }
}