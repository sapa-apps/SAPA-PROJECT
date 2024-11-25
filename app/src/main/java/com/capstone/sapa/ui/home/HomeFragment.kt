package com.capstone.sapa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.sapa.databinding.FragmentHomeBinding
import com.capstone.sapa.ui.history.HistoryAdapter
import com.capstone.sapa.ui.history.HistoryViewModel
import com.capstone.sapa.ui.speechToText.SpeechToTextActivity
import com.capstone.sapa.ui.textToSpeech.TextToSpeechActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
