package com.sapa.signlanguage.view.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sapa.signlanguage.data.db.HistoryDatabase
import com.sapa.signlanguage.data.db.TranslationHistory


class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = HistoryDatabase.getDatabase(application).historyDao()

    val recentHistory: LiveData<List<TranslationHistory>> = dao.getRecentHistory()
    val allHistory: LiveData<List<TranslationHistory>> = dao.getAllHistory()

    fun getHistoryById(id: Int): LiveData<TranslationHistory?> {
        return dao.getHistoryById(id)
    }
}

