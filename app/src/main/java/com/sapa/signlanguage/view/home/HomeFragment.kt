package com.sapa.signlanguage.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sapa.signlanguage.databinding.FragmentHomeBinding
import com.sapa.signlanguage.view.history.AllHistoryActivity
import com.sapa.signlanguage.view.history.DetailActivity
import com.sapa.signlanguage.view.history.HistoryAdapter
import com.sapa.signlanguage.view.history.HistoryViewModel
import com.sapa.signlanguage.view.speechToText.SpeechToTextActivity
import com.sapa.signlanguage.view.textToSpeech.TextToSpeechActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        adapter = HistoryAdapter { history ->
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("HISTORY_ID", history.id)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.recentHistory.observe(viewLifecycleOwner) { historyList ->
            adapter.submitList(historyList)
        }

        binding.viewAllButton.setOnClickListener {
            startActivity(Intent(activity, AllHistoryActivity::class.java))
        }

        // Menambahkan click listener untuk LinearLayout yang mengarah ke SpeechToTextActivity
        binding.btnSpeechToText.setOnClickListener {
            val intent = Intent(activity, SpeechToTextActivity::class.java)
            startActivity(intent)
        }

        binding.btnTextToSpeech.setOnClickListener {
            val intent = Intent(activity, TextToSpeechActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
