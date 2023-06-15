package com.example.cameraxexample.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cameraxexample.R
import com.example.cameraxexample.databinding.PreviewScanningFragmentBinding
import com.example.cameraxexample.viewmodel.MyViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PreviewScanningFragment() : Fragment() {

    private var _binding: PreviewScanningFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreviewScanningFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()


    }

    private fun bindView() {
        val imageView = binding.previewViewId.imageCaptured

        val savedUri: Uri =
            if (findNavController().currentDestination?.id == R.id.previewScanningFragment) {
                MyViewModel.savedUriFront
            } else {
                MyViewModel.savedUriBack
            }

        imageView.setImageURI(savedUri)
        binding.previewViewId.addMoreBtn.containerView.setOnClickListener {
            findNavController().navigateUp()

        }

        binding.previewViewId.doneBtn.containerView.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.previewScanningFragment) {
                findNavController().navigate(R.id.BackScanningFragment)
            } else {
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            }
        }
    }

}