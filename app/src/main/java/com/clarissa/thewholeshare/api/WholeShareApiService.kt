package com.clarissa.thewholeshare.api

import com.android.volley.toolbox.Volley
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue

class WholeShareApiService(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: WholeShareApiService? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: WholeShareApiService(context).also {
                    INSTANCE = it
                }
            }

        // Web service :
        val WS_HOST = "https://80c4-202-80-212-221.ap.ngrok.io/api"
    }
    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}