package com.sapa.signlanguage.data.remote

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://sapa-api-733973953931.asia-southeast2.run.app/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create()) // Untuk string mentah
        .addConverterFactory(GsonConverterFactory.create())   // Untuk JSON
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
