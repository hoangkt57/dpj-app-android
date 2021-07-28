package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.ChangePasswordCurrentUserQuery
import com.sonyged.hyperClass.UpdatePasswordMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ChangePasswordViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        var isRunning = false
    }

    val password = MutableLiveData<String>()

    fun isValidPassword(oldPass: String, newPass: String, newPassConfirm: String): Boolean {

        if (newPass != newPassConfirm) {
            status.postValue(Status(PASSWORD_ERROR_NOT_EQUAL_CONFIRM))
            return false
        }

        if (newPass.length < 8) {
            status.postValue(Status(PASSWORD_ERROR_LENGTH_8))
            return false
        }

        if (oldPass == newPass) {
            status.postValue(Status(PASSWORD_ERROR_SAME_OLD_PASS))
            return false
        }

        if (newPass.matches(Regex("[0-9]+"))) {
            status.postValue(Status(PASSWORD_ERROR_NEED_LETTER))
            return false
        }

        if (newPass.matches(Regex("^[a-zA-Z]*$"))) {
            status.postValue(Status(PASSWORD_ERROR_NEED_NUMBER))
            return false
        }

        return true
    }

    fun changePassword(userId: String, oldPass: String, newPass: String) {
        Timber.d("changePassword - isRunning: $isRunning")
        if (isRunning) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                isRunning = true
                val infoResponse = ApiUtils.getApolloClient().mutate(UpdatePasswordMutation(userId, oldPass, newPass)).await()
                Timber.d("changePassword - infoResponse: $infoResponse")
                isRunning = false
                if (!infoResponse.data?.userChangePassword?.asUserMutationErrors?.errors.isNullOrEmpty()) {
                    status.postValue(Status(PASSWORD_ERROR_INVALID))
                    return@launch
                } else {
                    status.postValue(Status(PASSWORD_ERROR_NONE))
                }
            } catch (e: Exception) {
                Timber.e(e, "changePassword")
            }
        }
    }

    fun loadCurrentPassword() {
        Timber.d("loadCurrentPassword")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val changePasswordResponse = ApiUtils.getApolloClient().query(ChangePasswordCurrentUserQuery()).await()
                Timber.d("loadCurrentPassword - changePasswordResponse: $changePasswordResponse")

                changePasswordResponse.data?.currentUser?.password?.let {
                    password.postValue(it)
                }
            } catch (e: Exception) {
                Timber.e(e, "loadCurrentPassword")
            }
        }
    }
}