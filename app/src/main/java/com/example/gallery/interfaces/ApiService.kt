package com.example.gallery.interfaces

import com.example.gallery.Pixabay
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    @GET("api/")
    fun getData(@Query("key") key:String,@Query("q") question:String,@Query("per_page") page:String):Call<Pixabay>

    @GET("api/")
    @JvmSuppressWildcards
    fun getData(@QueryMap map:Map<String,Any>) : Call<Pixabay>
}