package com.example.cameraxexample.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cameraxexample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraFragment : Fragment() {

    companion object {
        const val TAG = "BaseCameraFragment"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    lateinit var photoFile: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    open fun bindView() {

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            this.requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ), REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the directory for saving captured images
        outputDirectory = getOutputDirectoryFile()

        // Set up the executor for camera operations
        cameraExecutor = Executors.newSingleThreadExecutor()
        photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Set up the capture button click listener
        captureButton().setOnClickListener { captureImage() }
        backButton().setOnClickListener { backActionCallback() }

    }

    private fun getOutputDirectoryFile(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        // Create an instance of the ProcessCameraProvider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Get the ProcessCameraProvider instance
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(previewView()?.surfaceProvider) }

            // Set up the image capture use case
            imageCapture =
                ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build()

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
        lifecycleScope.launch(Dispatchers.IO) {

            val imageCapture = imageCapture ?: return@launch

            // Create a file with a timestamped name in the output directory
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat(
                    FILENAME_FORMAT,
                    Locale.US
                ).format(System.currentTimeMillis()) + ".jpg"
            )

            // Create a metadata object with rotation information
            val metadata = ImageCapture.Metadata()

            // Create an output options object which contains the file and metadata
            val outputOptions =
                ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata).build()

            // Capture the image
            imageCapture.takePicture(outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                        captureImageCallback(output)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Error capturing image", exception)
                    }
                })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    protected fun copyToTempFile(uri: Uri, tempFile: File): File {
        // Obtain an input stream from the uri
        val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to obtain input stream from URI")
        inputStream.use { inputStream ->
            FileOutputStream(tempFile).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        // Copy the stream to the temp file

        return tempFile
    }

    suspend fun compressImage(imageUri: Uri): File {
        return withContext(Dispatchers.IO) {
            val originalBitmap =
                BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(imageUri))
            val outputStream = ByteArrayOutputStream()
            originalBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                20,
                outputStream
            ) // Adjust compression quality as needed
            val byteArray = outputStream.toByteArray()

            val tempFile = File.createTempFile("compressed", ".jpg")
            FileOutputStream(tempFile).use { fileOutputStream ->
                fileOutputStream.write(byteArray)
            }

            tempFile
        }
    }

    fun encodeImageToBase64(imageFile: File): String {
        val byteArray = imageFile.readBytes()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    abstract fun captureImageCallback(output: ImageCapture.OutputFileResults)
    abstract fun backActionCallback()

    abstract fun previewView(): PreviewView?
    abstract fun captureButton(): View
    abstract fun backButton(): View
}