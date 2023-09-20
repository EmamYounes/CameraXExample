package com.example.cameraxexample.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.cameraxexample.adapter.GalleryAdapter
import com.example.cameraxexample.callbacks.ClickImageCallback
import com.example.cameraxexample.databinding.FragmentCameraBinding
import com.example.cameraxexample.enums.TypeEnum
import com.example.cameraxexample.model.GalleryModel
import kotlinx.coroutines.launch
import java.io.IOException

abstract class CameraFragment : BaseCameraFragment(), ClickImageCallback {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GalleryAdapter
    private var imagesList = mutableListOf<GalleryModel>()

    companion object {
        var position = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesList.addAll(imagesDamageCar())
        bindView()
    }

    override fun bindView() {
        super.bindView()
        handleButtonAction()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = GalleryAdapter(imagesList)
        adapter.clickImageCallback = this
        binding.previewViewId.galleryList.adapter = adapter
        binding.previewViewId.galleryList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.previewViewId.galleryList.setHasFixedSize(false)
    }

    private fun handleButtonAction() {
        binding.closeIcon.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.previewViewId.addMoreBtn.containerView.setOnClickListener {
            binding.captureViewId.captureLayout.visibility = View.VISIBLE
            binding.previewViewId.previewLayout.visibility = View.GONE
        }

        binding.previewViewId.doneBtn.containerView.setOnClickListener {
            setImagesDamageCar(imagesList)
            activity?.onBackPressed()
        }
    }

    private fun setVisibilityGalleryList() {
        handleMaxPhotoHint()
        if (imagesList.size > 1) binding.previewViewId.galleryList.visibility = View.VISIBLE
        else binding.previewViewId.galleryList.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSelectedImage(galleryModel: GalleryModel) {
        Glide.with(binding.root.context)
            .load(galleryModel.uri)
            .transform(CenterCrop()) // Apply any transformations you need
            .into(binding.previewViewId.imageCaptured)
        imagesList = adapter.getList()
        setVisibilityGalleryList()
    }

    override fun captureImageCallback(output: ImageCapture.OutputFileResults) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.captureViewId.captureLayout.visibility = View.GONE
            binding.previewViewId.previewLayout.visibility = View.VISIBLE
            val savedUri = output.savedUri ?: photoFile.toUri()
            val imageView = binding.previewViewId.imageCaptured

            Glide.with(binding.root.context)
                .load(savedUri)
                .transform(CenterCrop()) // Apply any transformations you need
                .into(imageView)
            imagesList.forEach {
                it.isChecked = false
            }

            try {
                val compressedImageFile = compressImage(savedUri)
                val base64 = encodeImageToBase64(compressedImageFile)

                imagesList.add(
                    GalleryModel(
                        savedUri,
                        true,
                        "damage_${position++}",
                        docBase64 = base64,
                        docTypeCode = docTypeCodeEnum()?.str
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }

            setVisibilityGalleryList()

            adapter.updateList(imagesList)

            val msg = "Image captured: $savedUri"
            Log.d(TAG, msg)
        }
    }

    private fun handleMaxPhotoHint() {
        if (imagesList.size >= 7) {
            binding.previewViewId.maxPhotoHint.visibility = View.VISIBLE
            binding.previewViewId.addMoreBtn.containerView.visibility = View.GONE
        } else {
            binding.previewViewId.maxPhotoHint.visibility = View.GONE
            binding.previewViewId.addMoreBtn.containerView.visibility = View.VISIBLE
        }
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


    abstract fun setImagesDamageCar(list: MutableList<GalleryModel>)
    abstract fun imagesDamageCar(): MutableList<GalleryModel>

    abstract fun docTypeCodeEnum(): TypeEnum?

}
