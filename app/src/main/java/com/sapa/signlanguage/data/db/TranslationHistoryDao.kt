package com.sapa.signlanguage.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TranslationHistoryDao {

    @Insert
    suspend fun insertAll(histories: List<TranslationHistory>)

    @Insert
    suspend fun insert(history: TranslationHistory)

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC LIMIT 5")
    fun getRecentHistory(): LiveData<List<TranslationHistory>>

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    fun getAllHistory(): LiveData<List<TranslationHistory>>

    @Query("SELECT * FROM history_table WHERE id = :id")
    fun getHistoryById(id: Int): LiveData<TranslationHistory?>
}

