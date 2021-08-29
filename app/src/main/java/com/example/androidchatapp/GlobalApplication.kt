package com.example.androidchatapp

import android.app.Application
import android.content.Context

class GlobalApplication : Application() {
    init {
        mInstance = this
    }
    companion object {
        private var mInstance: GlobalApplication? = null

        fun getContext(): Context {
            return mInstance!!.applicationContext
        }
    }
}