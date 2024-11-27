package com.sapa.signlanguage.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}