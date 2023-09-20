package com.example.cameraxexample.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.cameraxexample.R
import com.example.cameraxexample.databinding.PreviewScanningFragmentBinding
import com.example.cameraxexample.model.GalleryModel
import kotlin.math.abs

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
abstract class PreviewScanningFragment() : Fragment() {

    private var _binding: PreviewScanningFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PreviewScanningFragmentBinding.inflate(inflater, container, false)
        bindView()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindView() {
        if (isAdded) {

            val imageView = binding.previewViewId.imageCaptured

            binding.previewViewId.screenNumber.text = "1 of 2"
            var savedUri = imagesList()[0].uri
            binding.previewViewId.doneBtn.btnTitle.text = getString(R.string.next)

            if (!isFirstScreen()) {
                binding.previewViewId.screenNumber.text = "2 of 2"
                savedUri = imagesList()[1].uri
                binding.previewViewId.doneBtn.btnTitle.text = getString(R.string.done)
            }

            Glide.with(binding.root.context)
                .load(savedUri)
                .transform(CenterCrop()) // Apply any transformations you need
                .into(imageView)

            // Set up GestureDetector to detect swipe gestures
            val gestureDetector =
                GestureDetector(
                    requireContext(),
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onFling(
                            e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
                        ): Boolean {
                            val swipeThreshold =
                                100 // Adjust this value to control the sensitivity of the swipe
                            val swipeVelocityThreshold =
                                100 // Adjust this value to control the minimum swipe velocity

                            val diffX = e2.x - e1.x
                            val diffY = e2.y - e1.y

                            if (abs(diffX) > abs(diffY) && abs(diffX) > swipeThreshold && abs(
                                    velocityX
                                ) > swipeVelocityThreshold
                            ) {
                                if (diffX > 0) {
                                    // Swiped right
                                    showPreviousImage()
                                } else {
                                    // Swiped left
                                    showNextImage()
                                }
                                return true
                            }

                            return super.onFling(e1, e2, velocityX, velocityY)
                        }
                    })

// Set the touch listener on the ImageView
            imageView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }


            binding.previewViewId.addMoreBtn.containerView.setOnClickListener {
                findNavController().navigateUp()

            }

            binding.previewViewId.doneBtn.containerView.setOnClickListener {
                handleConfirmBtn()
            }

            binding.closeIcon.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    fun showPreviousImage() {
        binding.previewViewId.screenNumber.text = "1 of 2"
        Glide.with(binding.root.context)
            .load(imagesList()[0].uri)
            .transform(CenterCrop()) // Apply any transformations you need
            .into(binding.previewViewId.imageCaptured)
    }

    // Function to show the next image
    fun showNextImage() {
        binding.previewViewId.screenNumber.text = "2 of 2"
        if (imagesList().size > 1) {
            Glide.with(binding.root.context)
                .load(imagesList()[1].uri)
                .transform(CenterCrop()) // Apply any transformations you need
                .into(binding.previewViewId.imageCaptured)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun imagesList(): MutableList<GalleryModel>
    abstract fun handleConfirmBtn()
    abstract fun isFirstScreen(): Boolean
    abstract fun screenTitle(): String

}