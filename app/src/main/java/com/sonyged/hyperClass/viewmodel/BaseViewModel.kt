package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sonyged.hyperClass.constants.KEY_ERROR_MSG
import com.sonyged.hyperClass.constants.KEY_SUCCESS_MSG
import com.sonyged.hyperClass.constants.STATUS_FAILED
import com.sonyged.hyperClass.constants.STATUS_SUCCESSFUL
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

    protected fun sendErrorStatus(error: String) {
        val value = Status(STATUS_FAILED)
        value.extras.putString(KEY_ERROR_MSG, error)
        status.postValue(value)
    }

    protected fun sendSuccessStatus(text: String) {
        val value = Status(STATUS_SUCCESSFUL)
        value.extras.putString(KEY_SUCCESS_MSG, text)
        status.postValue(value)
    }

    protected fun sendErrorStatus(errors: List<Any>?) {
        val value = Status(STATUS_FAILED)
        val error = StringBuilder()
        errors?.forEach {
            error.append(Gson().toJson(it))
            error.append("\n")
        }
        value.extras.putString(KEY_ERROR_MSG, error.toString())
        status.postValue(value)
    }

}