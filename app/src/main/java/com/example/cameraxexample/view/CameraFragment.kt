package com.example.cameraxexample.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.cameraxexample.R
import com.example.cameraxexample.adapter.GalleryAdapter
import com.example.cameraxexample.callbacks.ClickImageCallback
import com.example.cameraxexample.databinding.FragmentCameraBinding
import com.example.cameraxexample.model.GalleryModel
import com.example.cameraxexample.viewmodel.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CameraFragment : BaseCameraFragment(), ClickImageCallback {

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var adapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
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
        viewLifecycleOwner.lifecycleScope.launch {
            Glide.with(binding.root.context)
                .load(galleryModel.uri)
                .into(binding.previewViewId.imageCaptured)
            MyViewModel.imagesList = adapter.getList()
            withContext(Dispatchers.Main) {
                setVisibilityGalleryList()
            }
        }
    }

    override fun captureImageCallback(output: ImageCapture.OutputFileResults) {
        viewLifecycleOwner.lifecycleScope.launch {
            val savedUri = output.savedUri ?: photoFile.toUri()

            // Perform data manipulation and updates
            val newGalleryModel = GalleryModel(savedUri, true)
            MyViewModel.imagesList.forEach {
                it.isChecked = false
            }
            MyViewModel.imagesList.add(newGalleryModel)

            // Ensure that the list size doesn't exceed the limit
            if (MyViewModel.imagesList.size > 6) {
                binding.previewViewId.addMoreBtn.containerView.visibility = View.GONE
            }

            // Update the adapter list
            withContext(Dispatchers.Main) {
                adapter.updateList(MyViewModel.imagesList)
            }

            // Set visibility for the gallery list
            setVisibilityGalleryList()

            // Notify the user about the image capture
            val msg = "Image captured: $savedUri"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            Log.d(TAG, msg)

            // Update the UI on the main thread
            updateUIOnMainThread()
        }
    }

    private fun updateUIOnMainThread() {
        requireActivity().runOnUiThread {
            binding.captureViewId.captureLayout.visibility = View.GONE
            binding.previewViewId.previewLayout.visibility = View.VISIBLE
            val savedUri = MyViewModel.imagesList.last().uri
            Glide.with(binding.root.context)
                .load(savedUri)
                .transform(CenterCrop()) // Apply any transformations you need
                .into(binding.previewViewId.imageCaptured)
        }
    }

    override fun previewView(): PreviewView {
        return binding.captureViewId.previewView
    }

    override fun captureButton(): View {
        return binding.captureViewId.captureButton
    }
}