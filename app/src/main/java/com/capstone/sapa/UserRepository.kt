package com.capstone.sapa

import com.capstone.sapa.pref.UserModel
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: com.capstone.sapa.pref.UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: com.capstone.sapa.pref.UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}