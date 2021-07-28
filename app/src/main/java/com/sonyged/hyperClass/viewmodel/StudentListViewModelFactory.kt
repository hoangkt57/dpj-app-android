package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StudentListViewModelFactory(private val application: Application, private val courseId: String, private val teacherId: String) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentListViewModel(application, courseId, teacherId) as T
    }
}