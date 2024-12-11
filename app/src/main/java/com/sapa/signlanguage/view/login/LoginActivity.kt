package com.sapa.signlanguage.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sapa.signlanguage.R
import com.sapa.signlanguage.databinding.ActivityLoginBinding
import com.sapa.signlanguage.view.ViewModelFactory
import com.sapa.signlanguage.view.main.MainActivity
import com.sapa.signlanguage.view.reset.ResetPasswordActivity
import com.sapa.signlanguage.view.signup.SignupActivity
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserModel
import com.sapa.signlanguage.data.pref.UserPreference
import com.sapa.signlanguage.data.pref.dataStore
import com.sapa.signlanguage.data.remote.ApiConfig
import com.sapa.signlanguage.data.remote.response.LoginRequest
import com.sapa.signlanguage.di.Injection
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private val userRepository: UserRepository by lazy {
        Injection.provideRepository(applicationContext)  // Menggunakan Injection untuk mendapatkan UserRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        setupSpannableTextForGuest()
        setupSpannableTextForRegister()
        setupSpannableText()
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

    private fun setupAction() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email atau password tidak boleh kosong")
                Log.d("LoginActivity", "Email atau password kosong")
            } else {
                Log.d("LoginActivity", "Memulai proses login dengan Firebase Authentication")
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginActivity", "Login Firebase berhasil")
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val firebaseToken = tokenTask.result?.token
                                    if (!firebaseToken.isNullOrEmpty()) {
                                        Log.d("LoginActivity", "Berhasil mendapatkan Firebase Token: $firebaseToken")
                                        loginToApi(firebaseToken)
                                    } else {
                                        showToast("Gagal mendapatkan token Firebase")
                                        Log.e("LoginActivity", "Token Firebase kosong")
                                    }
                                } else {
                                    val error = tokenTask.exception?.message
                                    showToast("Gagal mendapatkan token Firebase: $error")
                                    Log.e("LoginActivity", "Gagal mendapatkan token Firebase: $error")
                                }
                            }
                        } else {
                            val error = task.exception?.message
                            showToast("Login gagal: Email dan Password tidak cocok")
                            Log.e("LoginActivity", "Login Firebase gagal: $error")
                        }
                    }
            }
        }
    }

    private fun loginToApi(firebaseToken: String) {
        lifecycleScope.launch {
            Log.d("LoginActivity", "Memulai proses login ke API")
            try {
                val request = LoginRequest(token = firebaseToken) // Membuat body request dengan key token
                Log.d("LoginActivity", "Request login API: $request")

                val response = ApiConfig.apiService.login(request) // Panggilan API login
                Log.d("LoginActivity", "Response login API: $response")

                // Handle response
                val jwtToken = response.jwtToken // Langsung ambil token JWT dari response
                if (!jwtToken.isNullOrEmpty()) {
                    Log.d("LoginActivity", "JWT Token diterima: $jwtToken")
                    saveJwtToken(jwtToken) // Simpan token JWT
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    Log.d("LoginActivity", "Navigasi ke MainActivity")
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Login API berhasil, tetapi JWT token kosong")
                    Log.e("LoginActivity", "JWT token kosong")
                }
            } catch (e: Exception) {
                val error = e.localizedMessage
                showToast("Terjadi kesalahan saat login: $error")
                Log.e("LoginActivity", "Error login API: $error", e)
            }
        }
    }


    private suspend fun saveJwtToken(jwtToken: String) {
        val userRepository = UserRepository.getInstance(UserPreference.getInstance(dataStore))
        val user = UserModel(token = jwtToken, isLogin = true)
        userRepository.saveSession(user)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupSpannableText() {
        val resetText = SpannableString("Forgot your password? Reset Password")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Aksi ketika teks Reset Password diklik
                val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Hilangkan garis bawah
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.navy) // Warna teks
            }
        }

        resetText.setSpan(clickableSpan, 23, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.resetPasswordText.text = resetText
        binding.resetPasswordText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupSpannableTextForGuest() {
        val spannable = SpannableString("Continue as a Guest")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Autentikasi sebagai tamu menggunakan Firebase
                FirebaseAuth.getInstance().signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Jika autentikasi berhasil, kita mendapatkan UID pengguna tamu
                            val user = FirebaseAuth.getInstance().currentUser
                            Log.d("GuestAuth", "Guest authenticated with UID: ${user?.uid}")

                            lifecycleScope.launch {
                                val guestUser = UserModel(
                                    email = user?.email ?: "",
                                    token = user?.uid ?: "",
                                    isLogin = true,
                                    isGuest = true  // Menandakan status sebagai tamu
                                )
                                userRepository.saveGuestSession() // Menyimpan session guest
                            }

                            // Intent ke halaman utama sebagai tamu
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("isGuest", true) // Opsional, menandakan user sebagai tamu
                            startActivity(intent)

                            // Menutup SignupActivity setelah berhasil diarahkan ke halaman utama
                            finish()
                        } else {
                            // Jika autentikasi gagal, tampilkan pesan error
                            Log.e("GuestAuth", "Guest authentication failed", task.exception)
                        }
                    }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.navy) // Ubah warna teks
                ds.isUnderlineText = false // Menghapus underline
            }
        }

        // Menetapkan "Guest" sebagai teks klikabel
        spannable.setSpan(clickableSpan, 14, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Terapkan ke TextView
        binding.guestPrompt.text = spannable
        binding.guestPrompt.movementMethod = LinkMovementMethod.getInstance()
    }



    private fun setupSpannableTextForRegister() {
        val spannable = SpannableString("Don't have an account? Register Now")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Intent ke SignupActivity
                val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.navy) // Ubah warna teks
                ds.isUnderlineText = false // Menghapus underline
            }
        }

        // Menetapkan "Register Now" sebagai teks klikabel
        spannable.setSpan(clickableSpan, 23, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Terapkan ke TextView
        binding.registerPrompt.text = spannable
        binding.registerPrompt.movementMethod = LinkMovementMethod.getInstance()
    }


    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val message2 =
            ObjectAnimator.ofFloat(binding.messageTextView2, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.signInButton, View.ALPHA, 1f).setDuration(100)
        val orText = ObjectAnimator.ofFloat(binding.orText, View.ALPHA, 1f).setDuration(100)
        val dividerLeft =
            ObjectAnimator.ofFloat(binding.dividerLeft, View.ALPHA, 1f).setDuration(100)
        val dividerRight =
            ObjectAnimator.ofFloat(binding.dividerRight, View.ALPHA, 1f).setDuration(100)
        val signInButton2 =
            ObjectAnimator.ofFloat(binding.btnGoogle, View.ALPHA, 1f).setDuration(100)
        val guestPrompt =
            ObjectAnimator.ofFloat(binding.guestPrompt, View.ALPHA, 1f).setDuration(100)
        val registerPrompt =
            ObjectAnimator.ofFloat(binding.registerPrompt, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                message2,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                orText,
                dividerLeft,
                dividerRight,
                signInButton2,
                guestPrompt,
                registerPrompt
            )
            startDelay = 100
        }.start()
    }

}