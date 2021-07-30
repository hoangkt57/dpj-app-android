package com.sonyged.hyperClass.observer

import android.os.Bundle

class AppObserver {

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppObserver? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AppObserver().also { instance = it }
            }
    }

    private val observers = arrayListOf<Observer>()

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun removeAllObserver() {
        observers.clear()
    }

    fun sendEvent(event: Int) {
        observers.forEach {
            it.onEvent(event, null)
        }
    }

    fun sendEvent(event: Int, data: Bundle?) {
        observers.forEach {
            it.onEvent(event, data)
        }
    }

}