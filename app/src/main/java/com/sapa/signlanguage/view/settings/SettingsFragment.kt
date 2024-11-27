package com.sapa.signlanguage.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.databinding.FragmentSettingsBinding
import com.sapa.signlanguage.view.welcome.WelcomeActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()

        return root
    }

    private fun setupAction() {
        // Listener untuk LinearLayout Sign-out
        binding.signOutLayout.setOnClickListener {
            // Logout dari Firebase
            FirebaseAuth.getInstance().signOut()

            // Logout dari ViewModel (opsional jika diperlukan)
            // settingsViewModel.logout()

            // Redirect ke WelcomeActivity
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)

            // Menutup MainActivity (jika sedang di-back stack)
            requireActivity().finish()
        }
    }

}