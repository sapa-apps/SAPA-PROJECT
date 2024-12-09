package com.sapa.signlanguage.view.history

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sapa.signlanguage.R

class AllHistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_history)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        adapter = HistoryAdapter { history ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("HISTORY_ID", history.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allHistory.observe(this) { historyList ->
            adapter.submitList(historyList)
        }
    }
}
