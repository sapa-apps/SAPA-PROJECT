package com.sapa.signlanguage.view.settingsAccount

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sapa.signlanguage.data.remote.ApiConfig
import com.sapa.signlanguage.utils.Resource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SettingsAccountViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiConfig.apiService
    private var token: String? = null // Untuk menyimpan token

    val updateProfileLiveData = MutableLiveData<Resource<String>>()

    fun setToken(newToken: String) {
        token = newToken
    }

    fun updateProfile(
        nama: RequestBody,
        email: RequestBody,
        password: RequestBody,
        fotoProfil: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            updateProfileLiveData.postValue(Resource.Loading())
            try {
                val authHeader = "Bearer $token"
                Log.d("Settings", "Token: $authHeader") // Debug log token
                val response = apiService.updateProfile(authHeader, nama, email, password, fotoProfil)

                if (response.isSuccessful) {
                    updateProfileLiveData.postValue(Resource.Success("Profil berhasil diperbarui"))
                } else {
                    updateProfileLiveData.postValue(Resource.Error("Gagal memperbarui profil: ${response.message()}"))
                    Log.e("Settings", "Gagal memperbarui profil: ${response.message()}")
                }
            } catch (e: Exception) {
                updateProfileLiveData.postValue(Resource.Error("Terjadi kesalahan: ${e.message}"))
                Log.e("Settings", "Error: ${e.message}")
            }
        }
    }
}
