package com.sapa.signlanguage.view.speechToText

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserPreference
import com.sapa.signlanguage.data.pref.dataStore
import com.sapa.signlanguage.databinding.ActivitySpeechToTextBinding
import kotlinx.coroutines.launch

class SpeechToTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeechToTextBinding

    // Register the activity result launcher
    private val speechResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val speechResults =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.tvSpeechResult.text = speechResults?.get(0).orEmpty()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        val userRepository = UserRepository.getInstance(userPreference)

        lifecycleScope.launch {
            userRepository.getDarkMode().collect { isDarkMode ->
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        binding = ActivitySpeechToTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Button to capture speech
        binding.btnMic.setOnClickListener {
            val micGoogleIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            }

            try {
                // Gunakan launcher untuk memulai activity dan mendapatkan hasilnya
                speechResultLauncher.launch(micGoogleIntent)
                binding.tvSpeechResult.text = "" // Clear previous text
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    applicationContext,
                    "Maaf, Device Kamu Tidak Support Speech To Text",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }


}
