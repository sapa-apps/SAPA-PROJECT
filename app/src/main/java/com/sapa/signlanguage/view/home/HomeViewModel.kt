package com.sapa.signlanguage.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapa.signlanguage.data.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _profile = MutableLiveData<String>()
    val profile: LiveData<String> = _profile

    init {
        // Memanggil loadProfile saat ViewModel diinisialisasi
        loadProfile()
    }

    // Fungsi untuk memuat profil
    private fun loadProfile() {
        viewModelScope.launch {
            val profile = userRepository.getProfile().first()
            _profile.value = profile?.nama ?: "User"  // Menyimpan nama jika ada
            Log.d("HomeViewModel", "Profile data loaded: $profile")
        }
    }

    // Fungsi untuk memanggil ulang loadProfile saat login baru
    fun refreshProfile() {
        loadProfile()
    }
}

