package com.sapa.signlanguage.data

import com.sapa.signlanguage.data.pref.UserModel
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: com.sapa.signlanguage.data.pref.UserPreference
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
            userPreference: com.sapa.signlanguage.data.pref.UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}