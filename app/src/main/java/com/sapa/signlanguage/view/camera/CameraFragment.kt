package com.sapa.signlanguage.view.camera

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.firebase.auth.FirebaseAuth
import com.sapa.signlanguage.R
import com.sapa.signlanguage.view.login.LoginActivity
import java.util.*
import java.util.concurrent.Executors

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var previewView: PreviewView
    private lateinit var translationText: TextView
    private lateinit var textToSpeech: TextToSpeech

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Validasi apakah user login
        validateUser()

        // Inisialisasi komponen UI
        previewView = view.findViewById(R.id.previewView)
        translationText = view.findViewById(R.id.translationText)

        // Inisialisasi Text-to-Speech
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.forLanguageTag("id") // Bahasa Indonesia
            } else {
                Toast.makeText(requireContext(), "Text-to-Speech tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        // Meminta izin akses kamera jika belum diberikan
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startCamera()
        }
    }

    private fun validateUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null || currentUser.isAnonymous) {
            // Tampilkan dialog jika user adalah guest
            AlertDialog.Builder(requireContext())
                .setTitle("Fitur Memerlukan Login")
                .setMessage("Anda perlu login untuk menggunakan fitur ini. Apakah Anda ingin login sekarang?")
                .setPositiveButton("Login") { _, _ ->
                    // Arahkan ke halaman LoginActivity
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish() // Pastikan aktivitas dihentikan setelah login
                }
                .setNegativeButton("Kembali") { _, _ ->
                    // Kembali ke HomeFragment
                    findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                processImage(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val poseDetector = PoseDetection.getClient(
                PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build()
            )

            poseDetector.process(inputImage)
                .addOnSuccessListener { pose ->
                    val leftHand = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
                    val rightHand = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

                    if (leftHand != null && rightHand != null) {
                        val gesture = detectSign(leftHand, rightHand)
                        updateTranslationText(gesture)
                        speakText(gesture)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CameraFragment", "Pose detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun detectSign(leftHand: PoseLandmark, rightHand: PoseLandmark): String {
        return if (leftHand.position.x > rightHand.position.x - 50 &&
            leftHand.position.x < rightHand.position.x + 50) {
            "Halo"
        } else {
            "Gestur Tidak Dikenal"
        }
    }

    private fun updateTranslationText(gesture: String) {
        if (isAdded) { // Pastikan fragment masih terhubung dengan activity
            requireActivity().runOnUiThread {
                translationText.text = gesture
            }
        } else {
            Log.w("CameraFragment", "Fragment sudah detached, tidak dapat memperbarui UI.")
        }
    }

    private fun speakText(text: String) {
        if (text != "Gestur Tidak Dikenal") {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}

