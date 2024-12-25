package com.example.gallery.repository

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context:Context) {

    companion object {

        @Volatile private var instance : VolleySingleton? = null

        fun getInstance(context: Context) : VolleySingleton =
            instance ?: synchronized(this) {
                instance ?: VolleySingleton(context).also { instance = it }
            }
    }

    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

}