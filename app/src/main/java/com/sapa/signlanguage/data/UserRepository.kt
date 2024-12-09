package com.sapa.signlanguage.data

import com.sapa.signlanguage.data.pref.UserModel
import com.sapa.signlanguage.data.pref.UserPreference
import com.sapa.signlanguage.data.remote.ApiConfig.apiService
import com.sapa.signlanguage.data.remote.response.ProfileResponse
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference
) {

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        userPreference.saveDarkMode(isDarkMode)
    }

    fun getDarkMode(): Flow<Boolean> {
        return userPreference.getDarkMode()
    }

    suspend fun saveProfile(profile: ProfileResponse) {
        userPreference.saveProfile(profile)
    }

    fun getProfile(): Flow<ProfileResponse?> {
        return userPreference.getProfile()
    }

    suspend fun clearProfile() {
        userPreference.clearProfile()  // Pastikan ada fungsi untuk menghapus profil di UserPreference
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun clearGuestSession() {
        userPreference.clearGuestSession() // Panggil fungsi untuk menghapus sesi tamu
    }

    suspend fun saveJwtToken(jwtToken: String) {
        val user = UserModel(email = "", token = jwtToken, isLogin = true)
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveGuestSession() {
        val guestUser = UserModel(isLogin = false, isGuest = true)
        userPreference.saveSession(guestUser)  // Menyimpan status tamu
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}
