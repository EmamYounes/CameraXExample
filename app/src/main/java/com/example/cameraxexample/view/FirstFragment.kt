package com.example.cameraxexample.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cameraxexample.R
import com.example.cameraxexample.adapter.GalleryAdapter
import com.example.cameraxexample.callbacks.ClickImageCallback
import com.example.cameraxexample.databinding.FragmentFirstBinding
import com.example.cameraxexample.model.GalleryModel
import com.example.cameraxexample.viewmodel.MyViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseCameraFragment(), ClickImageCallback {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var adapter: GalleryAdapter

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

    override fun bindView() {
        super.bindView()
        handleButtonAction()
        initRecyclerView()
        // Set up the capture button click listener
        binding.captureViewId.captureButton.setOnClickListener { captureImage() }
    }

    private fun initRecyclerView() {
        adapter = GalleryAdapter(MyViewModel.imagesList)
        adapter.clickImageCallback = this
        binding.previewViewId.galleryList.adapter = adapter
        binding.previewViewId.galleryList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.previewViewId.galleryList.setHasFixedSize(false)
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
            findNavController().navigate(R.id.FrontScanningFragment)
        }

    }

    private fun setVisibilityGalleryList() {
        if (MyViewModel.imagesList.size > 1)
            binding.previewViewId.galleryList.visibility = View.VISIBLE
        else
            binding.previewViewId.galleryList.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    override fun onSelectedImage(galleryModel: GalleryModel) {
        binding.previewViewId.imageCaptured.setImageURI(galleryModel.uri)
        MyViewModel.imagesList = adapter.list
        setVisibilityGalleryList()
    }

    override fun captureImageCallback(output: ImageCapture.OutputFileResults) {
        binding.captureViewId.captureLayout.visibility = View.GONE
        binding.previewViewId.previewLayout.visibility = View.VISIBLE
        val savedUri = output.savedUri ?: photoFile.toUri()
        val imageView = binding.previewViewId.imageCaptured

        imageView.setImageURI(savedUri)
        MyViewModel.imagesList.forEach {
            it.isChecked = false
        }
        MyViewModel.imagesList.add(GalleryModel(savedUri, true))

        setVisibilityGalleryList()

        adapter.updateList(MyViewModel.imagesList)

        val msg = "Image captured: $savedUri"
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        Log.d(TAG, msg)
    }

    override fun previewView(): PreviewView {
        return binding.captureViewId.previewView
    }

}