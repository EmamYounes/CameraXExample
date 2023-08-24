package com.example.cameraxexample.view

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
import com.example.cameraxexample.R
import com.example.cameraxexample.databinding.FragmentScanningBinding
import com.example.cameraxexample.viewmodel.MyViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ScanningFragment : BaseCameraFragment() {

    private var _binding: FragmentScanningBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanningBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun bindView() {
        super.bindView()
        if (findNavController().currentDestination?.id == R.id.BackScanningFragment) {
            binding.title.text = "Car plate back"
        }
    }

    override fun captureImageCallback(output: ImageCapture.OutputFileResults) {
        viewLifecycleOwner.lifecycleScope.launch {

            val savedUri = output.savedUri ?: photoFile.toUri()
            if (findNavController().currentDestination?.id == R.id.FrontScanningFragment) {
                MyViewModel.savedUriFront = savedUri
                findNavController().navigate(R.id.previewScanningFragment)
            } else {
                MyViewModel.savedUriBack = savedUri
                findNavController().navigate(R.id.previewScanningFragment2)
            }
        }
    }

    override fun previewView(): PreviewView {
        return binding.captureViewId.previewView
    }

    override fun captureButton(): View {
        return binding.captureViewId.captureButton
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}