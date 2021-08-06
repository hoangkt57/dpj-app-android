package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.ChangePasswordCurrentUserQuery
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.UpdatePasswordMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ChangePasswordViewModel(application: Application) : BaseViewModel(application) {

    val password = MutableLiveData<String>()

    fun isValidPassword(oldPass: String, newPass: String, newPassConfirm: String): Boolean {
        val context = getApplication<Application>()
        if (newPass != newPassConfirm) {
            sendErrorStatus(context.getString(R.string.new_pass_not_equal_new_pass_1))
            return false
        }
        if (newPass.length < 8) {
            sendErrorStatus(context.getString(R.string.new_pass_least_8))
            return false
        }
        if (oldPass == newPass) {
            sendErrorStatus(context.getString(R.string.old_pass_equal_new_pass))
            return false
        }
        if (newPass.matches(Regex("[0-9]+"))) {
            sendErrorStatus(context.getString(R.string.new_pass_need_letter))
            return false
        }
        if (newPass.matches(Regex("^[a-zA-Z]*$"))) {
            sendErrorStatus(context.getString(R.string.new_pass_need_number))
            return false
        }
        return true
    }

    fun changePassword(userId: String, oldPass: String, newPass: String) {
        Timber.d("changePassword")
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val infoResponse = ApiUtils.getApolloClient().mutate(UpdatePasswordMutation(userId, oldPass, newPass)).await()
                Timber.d("changePassword - infoResponse: $infoResponse")
                val errors = infoResponse.data?.userChangePassword?.asUserMutationErrors?.errors
                if (errors.isNullOrEmpty()) {
                    sendSuccessStatus()
                    return@launch
                }
                sendErrorStatus(errors)
            } catch (e: Exception) {
                Timber.e(e, "changePassword")
                sendErrorStatus()
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