package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StudentViewModelFactory(private val application: Application, private val studentId: String) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentViewModel(application, studentId) as T
    }
}