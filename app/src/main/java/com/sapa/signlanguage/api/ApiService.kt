package com.sapa.signlanguage.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<Void>

    @GET("profile")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @PUT("profile/update")
    @Multipart
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("nama") nama: MultipartBody.Part?,
        @Part("emailBaru") emailBaru: MultipartBody.Part?,
        @Part fotoProfil: MultipartBody.Part?,
        @Part("passwordBaru") passwordBaru: MultipartBody.Part?
    ): Call<Void>
}