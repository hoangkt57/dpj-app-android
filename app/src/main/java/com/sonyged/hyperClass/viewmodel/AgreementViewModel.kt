package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.AgreementMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AgreementViewModel(application: Application) : BaseViewModel(application) {

    fun agreement() {
        Timber.d("agreement")
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val agreementResponse = ApiUtils.getApolloClient().mutate(AgreementMutation()).await()
                Timber.d("agreement - infoResponse: $agreementResponse")
                if (agreementResponse.data?.userAcceptTouAndPp?.asUserResult?.user?.acceptedTouAndPp == true) {
                    sendSuccessStatus()
                    return@launch
                }
                sendErrorStatus(agreementResponse.data?.userAcceptTouAndPp?.asUserMutationErrors?.errors)
            } catch (e: Exception) {
                Timber.e(e, "agreement")
                sendErrorStatus()
            }
        }
    }

}