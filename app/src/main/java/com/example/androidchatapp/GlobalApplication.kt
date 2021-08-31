package com.example.androidchatapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.androidchatapp.services.FirebaseAuthService

private const val TAG = "GlobalApplication"
class GlobalApplication : Application(), LifecycleObserver {
    init {
        mInstance = this
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d(TAG, "onAppForegrounded")
        FirebaseAuthService.activateChangeCurrentUser(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d(TAG, "onAppBackgrounded")
        FirebaseAuthService.activateChangeCurrentUser(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        Log.d(TAG, "onAppDestroyed")
        FirebaseAuthService.activateChangeCurrentUser(false)
    }

    companion object {
        private var mInstance: GlobalApplication? = null

        fun getContext(): Context {
            return mInstance!!.applicationContext
        }

    }
}
