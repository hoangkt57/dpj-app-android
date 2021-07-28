package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.model.Course

class CourseViewModelFactory(private val application: Application, private val courseId: String) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CourseViewModel(application, courseId) as T
    }
}