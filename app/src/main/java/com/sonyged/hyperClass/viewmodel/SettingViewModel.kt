package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageLayoutQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingViewModel(application: Application) : BaseViewModel(application) {

    val user = MutableLiveData<User>()

    init {
        loadData()
    }

    private fun loadData() {
        Timber.d("loadData")

        viewModelScope.launch(Dispatchers.Default) {
            try {

                val userResponse = ApiUtils.getApolloClient().query(PageLayoutQuery()).await()

                Timber.d("loadData - user : ${userResponse.data}")

                userResponse.data?.currentUser?.let {
                    user.postValue(User(it.id, it.loginId, it.name ?: "", it.password ?: "", it.email ?: ""))
                }

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun logout() {
        sharedPref.setToken("")
    }
}