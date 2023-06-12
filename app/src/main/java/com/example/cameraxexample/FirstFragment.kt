package com.example.cameraxexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cameraxexample.databinding.FragmentFirstBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "FirstFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {

        handleButtonAction()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the directory for saving captured images
        outputDirectory = getOutputDirectory()

        // Set up the executor for camera operations
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Set up the capture button click listener
        binding.captureViewId.captureButton.setOnClickListener { captureImage() }
    }

    private fun handleButtonAction() {
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.previewViewId.addMoreBtn.containerView.setOnClickListener {

            binding.captureViewId.captureLayout.visibility = View.VISIBLE
            binding.previewViewId.previewLayout.visibility = View.GONE
        }

        binding.previewViewId.doneBtn.containerView.setOnClickListener {
            Toast.makeText(requireContext(), "it's done", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }

    }

    private fun startCamera() {
        // Create an instance of the ProcessCameraProvider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Get the ProcessCameraProvider instance
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.captureViewId.previewView.surfaceProvider) }

            // Set up the image capture use case
            imageCapture = ImageCapture.Builder()
                .build()

            // Select the back camera as the default camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind any previous use cases before binding new ones
                cameraProvider.unbindAll()

                // Bind the camera use cases to the lifecycle
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error binding camera use cases", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        // Create a file with a timestamped name in the output directory
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        // Create a metadata object with rotation information
        val metadata = ImageCapture.Metadata()

        // Create an output options object which contains the file and metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        // Capture the image
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    binding.captureViewId.captureLayout.visibility = View.GONE
                    binding.previewViewId.previewLayout.visibility = View.VISIBLE
                    val savedUri = output.savedUri ?: photoFile.toUri()
                    binding.previewViewId.imageCaptured.setImageURI(savedUri)
                    val msg = "Image captured: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error capturing image", exception)
                }
            }
        )
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}