package com.sapa.signlanguage.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sapa.signlanguage.databinding.ActivityMainBinding
import com.sapa.signlanguage.view.ViewModelFactory
import com.sapa.signlanguage.view.welcome.WelcomeActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.R
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.remote.ApiConfig
import com.sapa.signlanguage.di.Injection
import com.sapa.signlanguage.view.home.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "MainActivity dimulai")

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        setupView()
        fetchAndSaveProfile()

        val userPreference = Injection.provideUserPreference(this)
        val userRepository = UserRepository.getInstance(userPreference)

        homeViewModel = ViewModelProvider(this, ViewModelFactory(userRepository)).get(HomeViewModel::class.java)

        // Memanggil refreshProfile untuk memastikan data profil terbaru diambil
        homeViewModel.refreshProfile()

        homeViewModel.profile.observe(this) { profileName ->
            Log.d("MainActivity", "Profile data observed: $profileName")
            // Anda dapat memperbarui UI di sini dengan data profil, misalnya di toolbar atau status bar
        }
    }

    private fun fetchAndSaveProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                if (!token.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val response = ApiConfig.apiService.getProfile("Bearer $token")
                            if (response.isSuccessful) {
                                response.body()?.let { profile ->
                                    viewModel.saveProfile(profile)  // Panggil fungsi ini untuk menyimpan profil
                                    Log.d("MainActivity", "Profil berhasil disimpan")
                                    Log.d("MainActivity", "Nama: ${profile.nama}")
                                }
                            } else {
                                Log.e("MainActivity", "Gagal mengambil profil: ${response.message()}")
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Kesalahan mengambil profil: ${e.message}")
                        }
                    }
                }
            } else {
                Log.e("MainActivity", "Gagal mendapatkan token Firebase")
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
