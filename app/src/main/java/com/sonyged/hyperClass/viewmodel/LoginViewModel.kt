package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageLayoutQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.LOGIN_CHECKING
import com.sonyged.hyperClass.constants.LOGIN_FAILED
import com.sonyged.hyperClass.constants.LOGIN_SUCCESSFUL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL


class LoginViewModel(application: Application) : BaseViewModel(application) {

    val state = MutableLiveData<Int>()

    fun isLogin(): Boolean {
        return sharedPref.getToken().isNotEmpty()
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
                val cookie = getCookie(URL("http://192.168.1.23:5000/graphql/"), "student0000@sctest", "indigo123")

                if (cookie.isNullOrEmpty()) {
                    state.postValue(LOGIN_FAILED)
                    return@launch
                }

                sharedPref.setToken(cookie)
                state.postValue(LOGIN_SUCCESSFUL)

            } catch (e: Exception) {
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
        urlConnection.readTimeout = 10000
        urlConnection.connectTimeout = 15000
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