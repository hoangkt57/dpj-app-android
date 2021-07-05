package com.sonyged.hyperClass

import android.app.Application
import android.content.Context
import com.sonyged.hyperClass.utils.CustomDebugTree
import timber.log.Timber

class MainApplication : Application() {

    init {
        mInstance = this
    }

    companion object {
        private lateinit var mInstance: MainApplication

        @JvmStatic
        fun getContext(): Context {
            return mInstance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(CustomDebugTree())

    }

}