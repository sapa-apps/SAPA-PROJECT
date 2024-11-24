package com.sapa.signlanguage.view.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.remote.ApiConfig
import com.sapa.signlanguage.data.remote.ApiService
import com.sapa.signlanguage.data.remote.response.ProfileResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _profileData = MutableLiveData<ProfileResponse?>()
    val profileData: LiveData<ProfileResponse?> = _profileData

    init {
        viewModelScope.launch {
            // Mengambil profil yang sudah disimpan di UserRepository
            userRepository.getProfile().collect { profile ->
                _profileData.postValue(profile)
            }
        }
    }
}


