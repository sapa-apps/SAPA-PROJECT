package com.sapa.signlanguage.di

import android.content.Context
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserPreference
import com.sapa.signlanguage.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = com.sapa.signlanguage.data.pref.UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}