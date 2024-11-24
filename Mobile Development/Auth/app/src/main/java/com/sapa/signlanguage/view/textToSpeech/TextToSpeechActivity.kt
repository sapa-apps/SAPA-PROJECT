package com.sapa.signlanguage.view.textToSpeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserPreference
import com.sapa.signlanguage.data.pref.dataStore
import com.sapa.signlanguage.databinding.ActivityTextToSpeechBinding
import kotlinx.coroutines.launch
import java.util.*

class TextToSpeechActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextToSpeechBinding
    private lateinit var textToSpeech: TextToSpeech
    private val idBahasaIndonesia = Locale("id", "ID") // Bahasa Indonesia locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// Mengamati pengaturan dark mode
        val userRepository = UserRepository.getInstance(UserPreference.getInstance(dataStore))
        lifecycleScope.launch {
            userRepository.getDarkMode().collect { isDarkMode ->
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        binding = ActivityTextToSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(idBahasaIndonesia)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Bahasa Indonesia tidak didukung!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Inisialisasi TextToSpeech gagal!", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle play button click
        binding.btnPlay.setOnClickListener {
            val text = binding.etInputText.text.toString()
            if (text.isNotEmpty()) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, "Silakan masukkan teks untuk dibaca!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
