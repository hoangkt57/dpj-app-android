package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.AgreementMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AgreementViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        var isRunning = false
    }

    val status = MutableLiveData<Int>()

    fun agreement() {
        Timber.d("agreement - isRunning: $isRunning")
        if (isRunning) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                isRunning = true
                val agreementResponse = ApiUtils.getApolloClient().mutate(AgreementMutation()).await()
                Timber.d("agreement - infoResponse: $agreementResponse")
                isRunning = false
                if (agreementResponse.data?.userAcceptTouAndPp?.asUserResult?.user?.acceptedTouAndPp == true) {
                    status.postValue(LOGIN_AGREEMENT_PP)
                    return@launch
                }
            } catch (e: Exception) {
                Timber.e(e, "changePassword")
            }
            status.postValue(LOGIN_FAILED)
        }
    }

}