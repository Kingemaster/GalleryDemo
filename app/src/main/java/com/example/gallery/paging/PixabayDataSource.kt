package com.example.gallery.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.gallery.NetWorkStatus
import com.example.gallery.PhotoItem
import com.example.gallery.Pixabay
import com.example.gallery.interfaces.ApiService
import com.example.gallery.repository.RetrofitSingleton
import com.example.gallery.viewmodel.KEY
import com.google.gson.Gson
import retrofit2.Callback

class PixabayDataSource : PageKeyedDataSource<Int,PhotoItem>() {

    var retryFun : (()->Any)? = null//定于函数类型的变量
    val netWorkStatus = MutableLiveData<NetWorkStatus>()
    private val keyWord = arrayOf("cat","dog","car","beauty","phone","computer","flower","animal").random()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        val service : ApiService = RetrofitSingleton.getInstance().create(ApiService::class.java)
        //val call = service.getData(KEY,keyWords.random(),"50")
        val map : MutableMap<String,Any> = mutableMapOf("key" to KEY,"q" to keyWord,"per_page" to 50,"page" to 1)
        val call = service.getData(map)
        netWorkStatus.postValue(NetWorkStatus.INITIAL)
        retryFun = null
        call.enqueue(object : Callback<Pixabay> {
            override fun onResponse(
                call: retrofit2.Call<Pixabay>,
                response: retrofit2.Response<Pixabay>
            ) {
                if (response.isSuccessful){
                    val dataList = response.body()?.hits?.toList() ?: emptyList()
                    callback.onResult(dataList,null,2)
                    netWorkStatus.postValue(NetWorkStatus.SUCCESS)
                }
            }

            override fun onFailure(call: retrofit2.Call<Pixabay>, exception: Throwable) {
                netWorkStatus.postValue(NetWorkStatus.FAIL)
                retryFun = {loadInitial(params,callback)}
                Log.d("yxc",exception.message?:"Error")
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        val service : ApiService = RetrofitSingleton.getInstance().create(ApiService::class.java)
        //val call = service.getData(KEY,keyWords.random(),"50")
        val map : MutableMap<String,Any> = mutableMapOf("key" to KEY,"q" to keyWord,"per_page" to 50,"page" to params.key)
        val call = service.getData(map)
        netWorkStatus.postValue(NetWorkStatus.LOADING)
        retryFun = null
        call.enqueue(object : Callback<Pixabay> {
            override fun onResponse(
                call: retrofit2.Call<Pixabay>,
                response: retrofit2.Response<Pixabay>
            ) {
                if (response.isSuccessful){
                    val dataList = response.body()?.hits?.toList() ?: emptyList()
                    callback.onResult(dataList,params.key + 1)
                    netWorkStatus.postValue(NetWorkStatus.SUCCESS)
                } else {
                    //异常就是全部加载完毕了
                    netWorkStatus.postValue(NetWorkStatus.COMPLETED)
                }
            }

            override fun onFailure(call: retrofit2.Call<Pixabay>, exception: Throwable) {
                netWorkStatus.postValue(NetWorkStatus.FAIL)
                retryFun = {loadAfter(params,callback)}
                Log.d("yxc",exception.message?:"Error")
            }

        })
    }

}