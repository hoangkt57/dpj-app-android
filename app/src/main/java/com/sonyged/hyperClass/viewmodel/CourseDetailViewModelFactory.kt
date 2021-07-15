package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CourseDetailViewModelFactory(private val application: Application, private val courseId: String) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CourseDetailViewModel(application, courseId) as T
    }
}