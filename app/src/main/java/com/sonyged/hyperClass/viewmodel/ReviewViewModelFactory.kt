package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewViewModelFactory(private val application: Application, private val studentWorkoutId: String, private val ids: List<String>) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReviewViewModel(application, studentWorkoutId, ids) as T
    }
}