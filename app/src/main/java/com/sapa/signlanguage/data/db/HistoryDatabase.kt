package com.sapa.signlanguage.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TranslationHistory::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): TranslationHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "history_database"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val data = listOf(
                                TranslationHistory(
                                    id = 1,
                                    originalText = "Halo",
                                    translatedText = "Hello",
                                    timestamp = System.currentTimeMillis()
                                ),
                                TranslationHistory(
                                    id = 2,
                                    originalText = "Apa kabar?",
                                    translatedText = "How are you?",
                                    timestamp = System.currentTimeMillis()
                                ),
                                TranslationHistory(
                                    id = 3,
                                    originalText = "Terima kasih",
                                    translatedText = "Thank you",
                                    timestamp = System.currentTimeMillis()
                                ),
                                TranslationHistory(
                                    id = 4,
                                    originalText = "Sampai jumpa",
                                    translatedText = "Goodbye",
                                    timestamp = System.currentTimeMillis()
                                ),
                                TranslationHistory(
                                    id = 5,
                                    originalText = "Selamat pagi",
                                    translatedText = "Good morning",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            getDatabase(context).historyDao().insertAll(data)
                            Log.d("DatabaseCallback", "Data inserted: $data")
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


