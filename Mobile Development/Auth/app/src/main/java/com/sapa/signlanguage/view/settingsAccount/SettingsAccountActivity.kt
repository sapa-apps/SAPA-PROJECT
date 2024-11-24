package com.sapa.signlanguage.view.settingsAccount

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.R
import com.sapa.signlanguage.utils.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SettingsAccountActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsAccountViewModel
    private lateinit var edtNama: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var imgProfile: ImageView
    private lateinit var btnUpdateProfile: Button
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_account)

        settingsViewModel = ViewModelProvider(this).get(SettingsAccountViewModel::class.java)
        edtNama = findViewById(R.id.edtNama)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        imgProfile = findViewById(R.id.imgProfile)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)

        getFirebaseToken { token ->
            settingsViewModel.setToken(token)
        }

        imgProfile.setOnClickListener {
            showUpdateProfileDialog()
        }

        settingsViewModel.updateProfileLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Tampilkan loading spinner jika diperlukan
                }
            }
        }

        btnUpdateProfile.setOnClickListener {
            val nama = edtNama.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            val namaPart = RequestBody.create("text/plain".toMediaTypeOrNull(), nama)
            val emailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val passwordPart = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

            selectedImageUri?.let { uri ->
                val realPath = getRealPathFromURI(uri)
                val fotoFile = if (realPath != null) {
                    File(realPath)
                } else {
                    // Jika path asli tidak ditemukan, salin file ke cache
                    val inputStream = contentResolver.openInputStream(uri)
                    val cacheFile = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input ->
                        cacheFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    cacheFile
                }

                if (fotoFile.exists()) {
                    val fotoProfilPart = fotoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val fotoProfil = MultipartBody.Part.createFormData("fotoProfil", fotoFile.name, fotoProfilPart)

                    // Log data yang akan dikirim
                    Log.d("Settings", "Mengirim: nama=$nama, email=$email, password=$password, foto=${fotoFile.name}")

                    settingsViewModel.updateProfile(namaPart, emailPart, passwordPart, fotoProfil)
                } else {
                    Toast.makeText(this, "File gambar tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Log.d("Settings", "Mengirim: nama=$nama, email=$email, password=$password, foto=TIDAK ADA")
                settingsViewModel.updateProfile(namaPart, emailPart, passwordPart, null)
            }
        }

    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }


    private fun showUpdateProfileDialog() {
        val options = arrayOf("Perbarui Foto Profil", "Batal")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Opsi")
        builder.setItems(options) { _, which ->
            if (which == 0) {
                openGallery()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let { uri ->
                    selectedImageUri = uri
                    imgProfile.setImageURI(uri)
                }
            }
        }

    private fun getFirebaseToken(onTokenReceived: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                if (!token.isNullOrEmpty()) {
                    onTokenReceived(token)
                } else {
                    Toast.makeText(this, "Token kosong, tidak bisa melanjutkan.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Gagal mendapatkan token.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "User tidak login.", Toast.LENGTH_SHORT).show()
        }
    }
}
