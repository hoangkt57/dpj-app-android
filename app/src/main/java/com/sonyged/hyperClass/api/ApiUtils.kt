package com.sonyged.hyperClass.api

import com.apollographql.apollo.ApolloClient
import com.sonyged.hyperClass.MainApplication
import com.sonyged.hyperClass.db.SharedPref
import okhttp3.OkHttpClient


class ApiUtils {

    companion object {

        private const val BASE_URL = "http://192.168.1.23:5000/graphql"

        fun getApolloClient(): ApolloClient {
            val okHttp = OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder().method(
                        original.method(),
                        original.body()
                    )

                    builder.addHeader(
                        "Cookie",
                        SharedPref.getInstance(MainApplication.getContext()).getToken()
                    )

                    chain.proceed(builder.build())
                }
                .build()

            return ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttp)
                .build()
        }

    }


}