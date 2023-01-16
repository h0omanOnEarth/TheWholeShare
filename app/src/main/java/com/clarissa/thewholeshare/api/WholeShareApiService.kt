package com.clarissa.thewholeshare.api

import com.android.volley.toolbox.Volley
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
//import com.clarissa.thewholeshare.BuildConfig

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

        /** Application Domain URLs */
        val DOMAIN: String = "https://c2c6-140-0-79-152.ap.ngrok.io"
        val WS_HOST: String = "${DOMAIN}/api"
        val WS_STORAGE_IMAGE: String = "${DOMAIN}/storage/images"

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