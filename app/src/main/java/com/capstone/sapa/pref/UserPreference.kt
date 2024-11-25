package com.capstone.sapa.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: com.capstone.sapa.pref.UserModel) {
        dataStore.edit { preferences ->
            preferences[com.capstone.sapa.pref.UserPreference.Companion.EMAIL_KEY] = user.email
            preferences[com.capstone.sapa.pref.UserPreference.Companion.TOKEN_KEY] = user.token
            preferences[com.capstone.sapa.pref.UserPreference.Companion.IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<com.capstone.sapa.pref.UserModel> {
        return dataStore.data.map { preferences ->
            com.capstone.sapa.pref.UserModel(
                preferences[com.capstone.sapa.pref.UserPreference.Companion.EMAIL_KEY]
                    ?: "",
                preferences[com.capstone.sapa.pref.UserPreference.Companion.TOKEN_KEY]
                    ?: "",
                preferences[com.capstone.sapa.pref.UserPreference.Companion.IS_LOGIN_KEY]
                    ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: com.capstone.sapa.pref.UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): com.capstone.sapa.pref.UserPreference {
            return com.capstone.sapa.pref.UserPreference.Companion.INSTANCE ?: synchronized(this) {
                val instance = com.capstone.sapa.pref.UserPreference(dataStore)
                com.capstone.sapa.pref.UserPreference.Companion.INSTANCE = instance
                instance
            }
        }
    }
}