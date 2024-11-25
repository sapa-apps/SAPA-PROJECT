package com.capstone.sapa.ui.login

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
import com.capstone.sapa.R
import com.capstone.sapa.databinding.ActivityLoginBinding
import com.capstone.sapa.ui.ViewModelFactory
import com.capstone.sapa.ui.main.MainActivity
import com.capstone.sapa.ui.reset.ResetPasswordActivity
import com.capstone.sapa.ui.signup.SignupActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

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
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginActivity", "Login berhasil: ${task.result?.user?.uid}")
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                Log.d("LoginActivity", "Memulai intent ke MainActivity")
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                showToast("Login berhasil, tapi user tidak ditemukan")
                            }
                        } else {
                            Log.e("LoginActivity", "Login gagal: ${task.exception?.message}")
                            showToast(task.exception?.message ?: "Login gagal")
                        }
                    }

            }
        }
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
                // Intent ke halaman utama sebagai tamu
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("isGuest", true) // Opsional, menandakan user sebagai tamu
                startActivity(intent)
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