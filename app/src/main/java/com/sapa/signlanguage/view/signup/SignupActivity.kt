package com.sapa.signlanguage.view.signup

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sapa.signlanguage.R
import com.sapa.signlanguage.databinding.ActivitySignupBinding
import com.sapa.signlanguage.view.login.LoginActivity
import com.sapa.signlanguage.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.data.pref.UserModel
import com.sapa.signlanguage.data.remote.ApiConfig
import com.sapa.signlanguage.data.remote.response.RegisterRequest
import com.sapa.signlanguage.di.Injection
import kotlinx.coroutines.launch
import retrofit2.Response


class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val userRepository: UserRepository by lazy {
        Injection.provideRepository(applicationContext)  // Menggunakan Injection untuk mendapatkan UserRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        setupSpannableText()
        setupSpannableTextForGuest()
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
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val password2 = binding.passwordEditText2.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || password2.isEmpty()) {
                showToast("Email atau password tidak boleh kosong")
            } else {
                val registerRequest = RegisterRequest(
                    nama = name,
                    email = email,
                    password = password
                )

                lifecycleScope.launch {
                    try {
                        val response = ApiConfig.apiService.register(registerRequest)
                        if (response.isSuccessful) {
                            // Ambil body respons sebagai string
                            val responseBody = response.body()
                            showToast(responseBody ?: "Pendaftaran berhasil!")
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val errorMessage = response.errorBody()?.string()
                            showToast("Pendaftaran gagal: $errorMessage")
                        }
                    } catch (e: Exception) {
                        showToast("Terjadi kesalahan: ${e.localizedMessage}")
                        Log.e("SignupActivity", "Terjadi kesalahan: ${e.localizedMessage}")
                    }
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    private fun setupSpannableText() {
        val spannable = SpannableString("Already have an account? Login")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Intent ke LoginActivity
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignupActivity, R.color.navy) // Ubah warna teks
                ds.isUnderlineText = false // Menghapus underline
            }
        }

        // Menetapkan "Login" sebagai teks klikabel
        spannable.setSpan(clickableSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Terapkan ke TextView
        binding.signupPrompt.text = spannable
        binding.signupPrompt.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupSpannableTextForGuest() {
        val spannable = SpannableString("Continue as a Guest")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Autentikasi sebagai tamu menggunakan Firebase
                FirebaseAuth.getInstance().signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            Log.d("GuestAuth", "Guest authenticated with UID: ${user?.uid}")

                            lifecycleScope.launch {
                                val guestUser = UserModel(
                                    email = user?.email ?: "",
                                    token = user?.uid ?: "",
                                    isLogin = true,
                                    isGuest = true  // Status tamu
                                )
                                userRepository.saveGuestSession()  // Menyimpan session tamu
                            }

                            startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Log.e("GuestAuth", "Guest authentication failed", task.exception)
                        }
                    }

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignupActivity, R.color.navy) // Ubah warna teks
                ds.isUnderlineText = false // Menghapus underline
            }
        }

        // Menetapkan "Guest" sebagai teks klikabel
        spannable.setSpan(clickableSpan, 14, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Terapkan ke TextView
        binding.guestPrompt1.text = spannable
        binding.guestPrompt1.movementMethod = LinkMovementMethod.getInstance()
    }




    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val title2 = ObjectAnimator.ofFloat(binding.titleTextView2, View.ALPHA, 1f).setDuration(100)
        val subtitle = ObjectAnimator.ofFloat(binding.subTitleTextView, View.ALPHA, 1f).setDuration(100)
        val subtitle2 = ObjectAnimator.ofFloat(binding.subTitleTextView2, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView2 =
            ObjectAnimator.ofFloat(binding.passwordTextView2, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout2 =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout2, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val signupPrompt = ObjectAnimator.ofFloat(binding.signupPrompt, View.ALPHA, 1f).setDuration(100)
        val guestPrompt = ObjectAnimator.ofFloat(binding.guestPrompt1, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                subtitle,
                subtitle2,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                passwordTextView2,
                passwordEditTextLayout2,
                signup,
                signupPrompt,
                guestPrompt
            )
            startDelay = 100
        }.start()
    }
}