package com.egabi.digitalsharjah_services.ui.RF_rafied.shared_components.view.camera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cameraxexample.databinding.FragmentScanningBinding
import com.example.cameraxexample.enums.TypeEnum
import com.example.cameraxexample.model.GalleryModel
import com.example.cameraxexample.view.BaseCameraFragment
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
abstract class ScanningFragment : BaseCameraFragment() {

    private var _binding: FragmentScanningBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanningBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun bindView() {
        super.bindView()

        if (isFirstScreen()) {
            binding.title.text = firstScreenTitle()
        } else {
            binding.title.text = secondScreenTitle()
        }

    }

    override fun captureImageCallback(output: ImageCapture.OutputFileResults) {
        viewLifecycleOwner.lifecycleScope.launch {
            val savedUri = output.savedUri ?: photoFile.toUri()
            val compressedImageFile = compressImage(savedUri)
            val base64 = encodeImageToBase64(compressedImageFile)
            if (isFirstScreen()) {
                manageFirstScreen(savedUri, base64, docTypeCodeEnumFront())
            } else {
                manageSecondScreen(savedUri, base64, docTypeCodeEnumBack())
            }
        }
    }

    private fun manageFirstScreen(
        savedUri: Uri,
        base64: String?,
        typeEnumFront: TypeEnum?
    ) {
        val model = GalleryModel(
            savedUri,
            true,
            firstPhotoName(),
            isFront = true,
            docBase64 = base64,
            docTypeCode = typeEnumFront?.str
        )

        if (imagesList().size > 0)
            imagesList()[0] = model
        else
            imagesList().add(0, model)

        findNavController().navigate(firstPreviewScreenID())
    }

    private fun manageSecondScreen(
        savedUri: Uri,
        base64: String?,
        typeEnumBack: TypeEnum?
    ) {
        val model = GalleryModel(
            savedUri,
            true,
            secondPhotoName(),
            isFront = false,
            docBase64 = base64,
            docTypeCode = typeEnumBack?.str
        )

        if (imagesList().size > 1)
            imagesList()[1] = model
        else
            imagesList().add(1, model)
        findNavController().navigate(secondPreviewScreenID())
    }

    override fun backActionCallback() {
        requireActivity().onBackPressed()
    }

    override fun previewView(): PreviewView {
        return binding.captureViewId.previewView
    }

    override fun captureButton(): View {
        return binding.captureViewId.captureButton
    }

    override fun backButton(): View {
        return binding.closeIcon
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun imagesList(): MutableList<GalleryModel>

    abstract fun isFirstScreen(): Boolean
    abstract fun firstPreviewScreenID(): Int
    abstract fun secondPreviewScreenID(): Int
    abstract fun firstScreenTitle(): String
    abstract fun secondScreenTitle(): String
    abstract fun firstPhotoName(): String
    abstract fun secondPhotoName(): String
    abstract fun docTypeCodeEnumFront(): TypeEnum?
    abstract fun docTypeCodeEnumBack(): TypeEnum?
}