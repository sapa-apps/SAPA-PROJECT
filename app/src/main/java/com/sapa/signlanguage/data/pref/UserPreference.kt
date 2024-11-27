package com.sapa.signlanguage.data.pref

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

    suspend fun saveSession(user: com.sapa.signlanguage.data.pref.UserModel) {
        dataStore.edit { preferences ->
            preferences[com.sapa.signlanguage.data.pref.UserPreference.Companion.EMAIL_KEY] = user.email
            preferences[com.sapa.signlanguage.data.pref.UserPreference.Companion.TOKEN_KEY] = user.token
            preferences[com.sapa.signlanguage.data.pref.UserPreference.Companion.IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<com.sapa.signlanguage.data.pref.UserModel> {
        return dataStore.data.map { preferences ->
            com.sapa.signlanguage.data.pref.UserModel(
                preferences[com.sapa.signlanguage.data.pref.UserPreference.Companion.EMAIL_KEY]
                    ?: "",
                preferences[com.sapa.signlanguage.data.pref.UserPreference.Companion.TOKEN_KEY]
                    ?: "",
                preferences[com.sapa.signlanguage.data.pref.UserPreference.Companion.IS_LOGIN_KEY]
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
        private var INSTANCE: com.sapa.signlanguage.data.pref.UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): com.sapa.signlanguage.data.pref.UserPreference {
            return com.sapa.signlanguage.data.pref.UserPreference.Companion.INSTANCE ?: synchronized(this) {
                val instance = com.sapa.signlanguage.data.pref.UserPreference(dataStore)
                com.sapa.signlanguage.data.pref.UserPreference.Companion.INSTANCE = instance
                instance
            }
        }
    }
}