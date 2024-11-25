package com.capstone.sapa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.sapa.ui.history.AllHistoryActivity
import com.capstone.sapa.ui.history.DetailActivity
import com.capstone.sapa.ui.history.HistoryAdapter
import com.capstone.sapa.ui.history.HistoryViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val viewAllButton: Button = findViewById(R.id.viewAllButton)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        adapter = HistoryAdapter { history ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("HISTORY_ID", history.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.recentHistory.observe(this) { historyList ->
            Log.d("MainActivity", "History List: $historyList")
            adapter.submitList(historyList)
        }

        viewAllButton.setOnClickListener {
            startActivity(Intent(this, AllHistoryActivity::class.java))
        }
    }
}




