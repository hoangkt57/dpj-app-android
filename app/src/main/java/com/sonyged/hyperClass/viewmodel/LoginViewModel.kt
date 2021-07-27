package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.AgreementCurrentUserQuery
import com.sonyged.hyperClass.ChangePasswordCurrentUserQuery
import com.sonyged.hyperClass.PageLayoutQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL


class LoginViewModel(application: Application) : BaseViewModel(application) {

    val state = MutableLiveData<Int>()

    var userId = ""

    fun isLogin(): Boolean {
        return sharedPref.getToken().isNotEmpty() && sharedPref.isLoginSuccess()
    }

    fun login(id: String?, password: String?) {
        Timber.d("login")
        state.value = LOGIN_CHECKING
        viewModelScope.launch(Dispatchers.Default) {

            if (!isDataValid(id, password)) {
                state.postValue(LOGIN_FAILED)
                return@launch
            }

            try {
                val cookie = getCookie(URL(ApiUtils.BASE_URL), id!!, password!!)

                if (cookie.isNullOrEmpty()) {
                    state.postValue(LOGIN_FAILED)
                    return@launch
                }

                sharedPref.setToken(cookie)

                val agreementResponse = ApiUtils.getApolloClient().query(AgreementCurrentUserQuery()).await()
                val changePasswordResponse = ApiUtils.getApolloClient().query(ChangePasswordCurrentUserQuery()).await()
                Timber.d("login - agreementResponse: $agreementResponse")
                Timber.d("login - changePasswordResponse: $changePasswordResponse")

                val agreementPP = agreementResponse.data?.currentUser?.acceptedTouAndPp ?: false
                val changePassword = changePasswordResponse.data?.currentUser?.password.isNullOrEmpty()

                userId = changePasswordResponse.data?.currentUser?.id ?: ""
                val isTeacher = changePasswordResponse.data?.currentUser?.__typename == TEACHER
                sharedPref.setTeacher(isTeacher)

                if (userId.isEmpty()) {
                    state.postValue(LOGIN_FAILED)
                    return@launch
                }

                sharedPref.setUserId(userId)

                if (agreementPP && changePassword) {
                    state.postValue(LOGIN_SUCCESSFUL)
                } else if (agreementPP && !changePassword) {
                    state.postValue(LOGIN_CHANGE_PASSWORD)
                } else if (!agreementPP && changePassword) {
                    state.postValue(LOGIN_AGREEMENT_PP)
                } else {
                    state.postValue(LOGIN_BOTH)
                }

            } catch (e: Exception) {
                Timber.e(e, "login")
                state.postValue(LOGIN_FAILED)
            }
        }
    }

    private fun isDataValid(id: String?, password: String?): Boolean {
        if (id.isNullOrEmpty() || password.isNullOrEmpty()) {
            return false
        }
        return true
    }

    fun currentUser() {
        Timber.d("currentUser")
        viewModelScope.launch(Dispatchers.Default) {

            val response = ApiUtils.getApolloClient().query(PageLayoutQuery()).await()

            Timber.d("currentUser - ${response}")

        }
    }

    private fun getCookie(url: URL, id: String, password: String): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.readTimeout = 30000
        urlConnection.connectTimeout = 30000
        urlConnection.requestMethod = "POST"
        urlConnection.doOutput = true
        urlConnection.doInput = true
        urlConnection.setRequestProperty("Content-Type", "application/json")
        val wr = DataOutputStream(urlConnection.outputStream)
        wr.writeBytes(
            "{\n" +
                    "    \"query\": \"mutation {signin(input: {loginId:\\\"$id\\\",password:\\\"$password\\\"}) { user {id name }error }} \"\n" +
                    "}"
        )
        wr.flush()
        wr.close()
        val rc = urlConnection.responseCode

        val cookie = urlConnection.getHeaderField("Set-Cookie")

        Timber.d("getCookie - cookie: $cookie")
        return cookie
    }

}