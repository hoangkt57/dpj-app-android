package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sonyged.hyperClass.db.SharedPref
import com.sonyged.hyperClass.model.Status

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val sharedPref = SharedPref.getInstance(application)

    val status = MutableLiveData<Status>()

    fun isTeacher(): Boolean {
        return sharedPref.isTeacher()
    }

    fun setLoginSuccess() {
        sharedPref.setLoginSuccess(true)
    }

}