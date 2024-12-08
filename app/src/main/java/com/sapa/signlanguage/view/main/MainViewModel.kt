package com.sapa.signlanguage.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserModel
import com.sapa.signlanguage.data.remote.response.ProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _profileData = MutableLiveData<ProfileResponse?>()
    val profileData: LiveData<ProfileResponse?> get() = _profileData

    init {
        viewModelScope.launch {
            userRepository.getProfile().collect { profile ->
                _profileData.postValue(profile)
            }
        }
    }

    fun saveProfile(profile: ProfileResponse) {
        viewModelScope.launch {
            userRepository.saveProfile(profile)
        }
    }
}
