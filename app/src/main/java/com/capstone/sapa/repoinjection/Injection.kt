package com.capstone.sapa.repoinjection

import android.content.Context
import com.capstone.sapa.UserRepository
import com.capstone.sapa.pref.dataStore


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = com.capstone.sapa.pref.UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}