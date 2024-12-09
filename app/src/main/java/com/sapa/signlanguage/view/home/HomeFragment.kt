package com.sapa.signlanguage.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sapa.signlanguage.R
import com.sapa.signlanguage.databinding.FragmentHomeBinding
import com.sapa.signlanguage.view.speechToText.SpeechToTextActivity
import com.sapa.signlanguage.view.textToSpeech.TextToSpeechActivity
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserPreference
import com.sapa.signlanguage.di.Injection
import com.sapa.signlanguage.view.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository
    private lateinit var homeViewModel: HomeViewModel

    private val TAG = "HomeFragment" // Tag untuk log

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi UserPreference menggunakan Context
        val userPreference = Injection.provideUserPreference(requireContext()) // Menggunakan getInstance
        userRepository = UserRepository.getInstance(userPreference)

        // Gunakan ViewModelProvider dengan factory untuk menyuntikkan userRepository
        homeViewModel = ViewModelProvider(this, ViewModelFactory(userRepository)).get(HomeViewModel::class.java)

        // Menambahkan click listener untuk LinearLayout yang mengarah ke SpeechToTextActivity
        binding.btnSpeechToText.setOnClickListener {
            val intent = Intent(activity, SpeechToTextActivity::class.java)
            startActivity(intent)
        }

        binding.btnTextToSpeech.setOnClickListener {
            val intent = Intent(activity, TextToSpeechActivity::class.java)
            startActivity(intent)
        }

        // Mengambil reference dari TextView dan mengobservasi data profil
        val greetingsTextView = binding.headerGreeting

        homeViewModel.profile.observe(viewLifecycleOwner) { profileName ->
            Log.d(TAG, "Profile data observed: $profileName")
            greetingsTextView.text = getString(R.string.header_greetings, profileName)
        }
        homeViewModel.refreshProfile()

        return root
    }

    override fun onResume() {
        super.onResume()
        // Pastikan ViewModel memuat ulang profil
        homeViewModel.refreshProfile()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



