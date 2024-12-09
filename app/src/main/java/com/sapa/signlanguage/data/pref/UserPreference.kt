package com.sapa.signlanguage.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.sapa.signlanguage.data.remote.response.ProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
    private val IS_GUEST_KEY = booleanPreferencesKey("isGuest")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val PROFILE_KEY = stringPreferencesKey("profile_key")

    // Fungsi untuk menghapus sesi tamu
    suspend fun clearGuestSession() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGIN_KEY)
            preferences.remove(IS_GUEST_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = user.isLogin
            preferences[IS_GUEST_KEY] = user.isGuest  // Menyimpan status guest
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                preferences[IS_GUEST_KEY] ?: false // Mengambil status guest
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    // Menyimpan status dark mode
    private val IS_DARK_MODE_KEY = booleanPreferencesKey("isDarkMode")

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDarkMode
        }
    }

    fun getDarkMode(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: false
        }
    }

    suspend fun saveProfile(profile: ProfileResponse) {
        val profileJson = Gson().toJson(profile)
        dataStore.edit { preferences ->
            preferences[PROFILE_KEY] = profileJson
        }
    }

    fun getProfile(): Flow<ProfileResponse?> {
        return dataStore.data.map { preferences ->
            preferences[PROFILE_KEY]?.let {
                Gson().fromJson(it, ProfileResponse::class.java)
            }
        }
    }

    suspend fun clearProfile() {
        dataStore.edit { preferences ->
            preferences.remove(PROFILE_KEY) // Menghapus data profil
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val IS_GUEST_KEY = booleanPreferencesKey("isGuest")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

