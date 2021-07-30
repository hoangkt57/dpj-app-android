package com.sonyged.hyperClass.api

import com.apollographql.apollo.ApolloClient
import com.sonyged.hyperClass.MainApplication
import com.sonyged.hyperClass.db.SharedPref
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class ApiUtils {

    companion object {

        const val BASE_URL = "http://192.168.0.106:5000/graphql"
//        const val BASE_URL = "https://api.hyperclass.jp/graphql"

        fun getApolloClient(): ApolloClient {
            val okHttp = OkHttpClient
                .Builder()
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
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