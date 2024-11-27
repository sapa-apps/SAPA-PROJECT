package com.sapa.signlanguage.view.history

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sapa.signlanguage.R

class DetailActivity : AppCompatActivity() {
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val originalText: TextView = findViewById(R.id.originalText)
        val translatedText: TextView = findViewById(R.id.translatedText)

        val historyId = intent.getIntExtra("HISTORY_ID", -1)
        if (historyId == -1) {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        viewModel.getHistoryById(historyId).observe(this) { history ->
            history?.let {
                originalText.text = it.originalText
                translatedText.text = it.translatedText
            }
        }
    }
}
