package com.sapa.signlanguage.data.remote

import com.sapa.signlanguage.data.remote.response.ContentListResponse
import com.sapa.signlanguage.data.remote.response.ContentResponse
import com.sapa.signlanguage.data.remote.response.LoginRequest
import com.sapa.signlanguage.data.remote.response.LoginResponse
import com.sapa.signlanguage.data.remote.response.ProfileResponse
import com.sapa.signlanguage.data.remote.response.RegisterRequest
import com.sapa.signlanguage.data.remote.response.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): retrofit2.Response<String>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): retrofit2.Response<ProfileResponse>

    @Multipart
    @PUT("profile/update")
    suspend fun updateProfile(
        @Header("Authorization") auth: String,
        @Part("nama") nama: RequestBody,
        @Part("emailBaru") email: RequestBody,
        @Part("passwordBaru") password: RequestBody,
        @Part images: MultipartBody.Part?
    ): retrofit2.Response<UpdateProfileResponse>

    // Create Content
    @Multipart
    @POST("contents")
    suspend fun createContent(
        @Part("title") title: String,
        @Part("description") description: String,
        @Part images: MultipartBody.Part
    ): retrofit2.Response<ContentResponse>

    // Get All Content
    @GET("contents")
    suspend fun getAllContent(): retrofit2.Response<ContentListResponse>

    // Get Content By ID
    @GET("contents/{id}")
    suspend fun getContentById(
        @Path("id") id: String
    ): retrofit2.Response<ContentResponse>

    // Update Content
    @Multipart
    @PUT("contents/{id}")
    suspend fun updateContent(
        @Path("id") id: String,
        @Part("title") title: String,
        @Part("description") description: String,
        @Part images: MultipartBody.Part
    ): retrofit2.Response<ContentResponse>

    // Delete Content
    @DELETE("contents/{id}")
    suspend fun deleteContent(
        @Path("id") id: String
    ): retrofit2.Response<Unit>

}

