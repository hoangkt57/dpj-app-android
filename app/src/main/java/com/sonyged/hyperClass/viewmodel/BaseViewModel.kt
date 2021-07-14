package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sonyged.hyperClass.db.SharedPref

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val sharedPref = SharedPref.getInstance(application)

    fun isTeacher() : Boolean {
        return sharedPref.isTeacher()
    }

    fun setLoginSuccess() {
        sharedPref.setLoginSuccess(true)
    }

}