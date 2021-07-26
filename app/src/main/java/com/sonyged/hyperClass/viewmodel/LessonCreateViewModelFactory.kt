package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.model.Lesson

class LessonCreateViewModelFactory(
    private val application: Application,
    private val lesson: Lesson
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LessonCreateViewModel(application, lesson) as T
    }
}