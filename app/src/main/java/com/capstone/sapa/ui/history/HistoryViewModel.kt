package com.capstone.sapa.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.capstone.sapa.db.HistoryDatabase
import com.capstone.sapa.db.TranslationHistory

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = HistoryDatabase.getDatabase(application).historyDao()

    val recentHistory: LiveData<List<TranslationHistory>> = dao.getRecentHistory()
    val allHistory: LiveData<List<TranslationHistory>> = dao.getAllHistory()

    fun getHistoryById(id: Int): LiveData<TranslationHistory?> {
        return dao.getHistoryById(id)
    }
}

