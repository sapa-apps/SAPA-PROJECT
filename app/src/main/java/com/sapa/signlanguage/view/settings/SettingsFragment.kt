package com.sapa.signlanguage.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.data.UserRepository
import com.sapa.signlanguage.databinding.FragmentSettingsBinding
import com.sapa.signlanguage.di.Injection
import com.sapa.signlanguage.view.settingsAccount.SettingsAccountActivity
import com.sapa.signlanguage.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log
import com.sapa.signlanguage.view.ViewModelFactory

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var settingsViewModel: SettingsViewModel
    private val userRepository: UserRepository by lazy {
        Injection.provideRepository(requireContext())
    }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi ViewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory(userRepository))[SettingsViewModel::class.java]

        // Periksa sesi tamu dan batasi akses jika perlu
        checkSession()

        // Menambahkan listener untuk layout yang mengarahkan ke SettingsAccountActivity
        val settingsAccountLayout: LinearLayout = binding.settingsAccountLayout
        settingsAccountLayout.setOnClickListener {
            val intent = Intent(requireContext(), SettingsAccountActivity::class.java)
            startActivity(intent)
        }

        // Mengatur Switch sesuai Dark Mode dari DataStore
        lifecycleScope.launch {
            val isDarkMode = userRepository.getDarkMode().first()
            binding.switchNightMode.isChecked = isDarkMode
        }

        // Menambahkan listener untuk Switch Night Mode
        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                userRepository.saveDarkMode(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        // Mengamati perubahan data profil
        settingsViewModel.profileData.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                // Menampilkan data profil pada UI
                binding.textViewNama.text = profile.nama
                binding.textViewEmail.text = profile.email
                Glide.with(this).load(profile.fotoProfil).into(binding.imageViewFotoProfil)
            } else {
                Log.e("SettingsFragment", "Profil gagal dimuat")
            }
        }

        setupAction()

        return root
    }

    private fun checkSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            userRepository.getSession().collect { user ->
                if (user.isGuest) {
                    // Jika pengguna adalah tamu, tampilkan pesan dan alihkan
                    Toast.makeText(
                        requireContext(),
                        "Fitur Settings Account & Night Mode hanya untuk user terdaftar.",
                        Toast.LENGTH_LONG
                    ).show()
                    disableAccountAccess()
                }
            }
        }
    }

    private fun disableAccountAccess() {
        // Nonaktifkan layout atau tombol untuk akun
        binding.settingsAccountLayout.isEnabled = false
        binding.switchNightMode.isEnabled = false
        binding.settingsAccountLayout.alpha = 0.5f // Memberi efek disable pada layout
    }

    private fun setupAction() {
        binding.signOutLayout.setOnClickListener {
            // Logout dari Firebase
            FirebaseAuth.getInstance().signOut()

            // Logout dari DataStore (UserPreference)
            lifecycleScope.launch {
                userRepository.logout()
            }

            // Pastikan tidak ada session aktif dan arahkan ke WelcomeActivity
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)

            // Menutup MainActivity atau fragment yang sedang aktif
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
