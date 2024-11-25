package com.capstone.sapa.ui.signup

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
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.sapa.R
import com.capstone.sapa.databinding.ActivitySignupBinding
import com.capstone.sapa.ui.login.LoginActivity
import com.capstone.sapa.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

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
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email atau password tidak boleh kosong")
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Akun berhasil dibuat
                            AlertDialog.Builder(this).apply {
                                setTitle("Pendaftaran Berhasil!")
                                setMessage("Silakan login menggunakan akun Anda.")
                                setPositiveButton("OK") { _, _ ->
                                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        } else {
                            // Tampilkan error
                            showToast(task.exception?.message ?: "Pendaftaran gagal")
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
                // Intent ke halaman utama sebagai tamu
                val intent = Intent(this@SignupActivity, MainActivity::class.java)
                intent.putExtra("isGuest", true) // Opsional, menandakan user sebagai tamu
                startActivity(intent)
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