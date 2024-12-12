package com.sapa.signlanguage.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.databinding.ActivityWelcomeBinding
import com.sapa.signlanguage.view.login.LoginActivity
import com.sapa.signlanguage.view.signup.SignupActivity
import com.sapa.signlanguage.view.main.MainActivity
import kotlinx.coroutines.launch
import com.sapa.signlanguage.di.Injection
import kotlinx.coroutines.flow.first

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val userRepository: UserRepository by lazy {
        Injection.provideRepository(applicationContext)  // Menggunakan Injection untuk mendapatkan UserRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val isDarkMode = userRepository.getDarkMode().first()
            toggleDarkMode(isDarkMode)
        }

        setupView()
        setupAction()
        checkSession()
    }

    private fun toggleDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun checkSession() {
        lifecycleScope.launch {
            userRepository.getSession().collect { user ->
                Log.d("WelcomeActivity", "isLogin: ${user.isLogin}, isGuest: ${user.isGuest}")
                if (isFinishing) return@collect // Pastikan Activity belum dihancurkan

                if (user.isLogin) {
                    goToHomePage()
                } else if (user.isGuest) {
                    goToHomePageAsGuest()
                }
            }
        }
    }

    private fun goToHomePageAsGuest() {
        if (isFinishing) return
        startActivity(Intent(this, MainActivity::class.java))
//        finish()
    }

    private fun goToHomePage() {
        if (isFinishing) return
        startActivity(Intent(this, MainActivity::class.java))
//        finish()
    }

    private fun setupView() {
        if (isFinishing) return

        // Cek jika status bar perlu disembunyikan
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            if (isFinishing) return@setOnClickListener
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            if (isFinishing) return@setOnClickListener
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}