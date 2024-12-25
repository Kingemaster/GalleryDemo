package com.example.gallery.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.gallery.PhotoItem
import com.example.gallery.Pixabay
import com.example.gallery.interfaces.ApiService
import com.example.gallery.repository.RetrofitSingleton
import com.example.gallery.repository.VolleySingleton
import com.google.gson.Gson
import retrofit2.Callback

const val KEY = "47665960-d6164fa27139943e25f580231"

class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val _photoList  = MutableLiveData<List<PhotoItem>>()
    private val keyWords = arrayOf("cat","dog","car","beauty","phone","computer","flower","animal")

    val photoList : LiveData<List<PhotoItem>>
        get() = _photoList

    fun loadData(){
        val stringRequest = StringRequest(Request.Method.GET, getUrl(),
            Response.Listener {
                _photoList.value = Gson().fromJson(it, Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.d("yxc",it.message?:"Error")
            }
        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl():String = "https://pixabay.com/api/?key=47665960-d6164fa27139943e25f580231&q=${keyWords.random()}&per_page=50"

    fun loadRetrofitData(){
        val service : ApiService = RetrofitSingleton.getInstance().create(ApiService::class.java)
        //val call = service.getData(KEY,keyWords.random(),"50")
        val map : MutableMap<String,Any> = mutableMapOf("key" to KEY,"q" to keyWords.random(),"per_page" to 50)
        val call = service.getData(map)
        call.enqueue(object : Callback<Pixabay>{
            override fun onResponse(
                call: retrofit2.Call<Pixabay>,
                response: retrofit2.Response<Pixabay>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    _photoList.value = res?.hits?.toList()
                }
            }

            override fun onFailure(call: retrofit2.Call<Pixabay>, exception: Throwable) {
                Log.d("yxc",exception.message?:"Error")
            }

        })
    }
}