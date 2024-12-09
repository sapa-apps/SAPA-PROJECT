package com.sapa.signlanguage.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class TranslationHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val translatedText: String,
    val timestamp: Long
)

