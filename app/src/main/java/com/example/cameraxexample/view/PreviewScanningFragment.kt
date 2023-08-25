package com.example.cameraxexample.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.cameraxexample.R
import com.example.cameraxexample.databinding.PreviewScanningFragmentBinding
import com.example.cameraxexample.viewmodel.MyViewModel
import kotlin.math.abs

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

    @SuppressLint("ClickableViewAccessibility")
    private fun bindView() {
        val imageView = binding.previewViewId.imageCaptured

        val savedUri: Uri =
            if (findNavController().currentDestination?.id == R.id.previewScanningFragment) {
                MyViewModel.savedUriFront
            } else {
                MyViewModel.savedUriBack
            }

        Glide.with(binding.root.context)
            .load(savedUri)
            .transform(CenterCrop()) // Apply any transformations you need
            .into(imageView)

        // Set up GestureDetector to detect swipe gestures
        val gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val swipeThreshold =
                        100 // Adjust this value to control the sensitivity of the swipe
                    val swipeVelocityThreshold =
                        100 // Adjust this value to control the minimum swipe velocity

                    val diffX = e2.x - e1.x
                    val diffY = e2.y - e1.y

                    if (abs(diffX) > abs(diffY)
                        && abs(diffX) > swipeThreshold
                        && abs(velocityX) > swipeVelocityThreshold
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
            if (findNavController().currentDestination?.id == R.id.previewScanningFragment) {
                findNavController().navigate(R.id.BackScanningFragment)
            } else {
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showPreviousImage() {
        Glide.with(binding.root.context)
            .load(MyViewModel.savedUriFront)
            .transform(CenterCrop()) // Apply any transformations you need
            .into(binding.previewViewId.imageCaptured)
    }

    // Function to show the next image
    fun showNextImage() {
        if (MyViewModel.isSavedUriBackInit()) {
            Glide.with(binding.root.context)
                .load(MyViewModel.savedUriBack)
                .transform(CenterCrop()) // Apply any transformations you need
                .into(binding.previewViewId.imageCaptured)
        }
    }

}