package com.example.cameraxexample.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        imageView.setImageURI(savedUri)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

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
        binding.previewViewId.imageCaptured.setImageURI(MyViewModel.savedUriFront)
        binding.previewViewId.imageCaptured.scaleType = ImageView.ScaleType.CENTER_CROP

    }

    // Function to show the next image
    fun showNextImage() {
        if (MyViewModel.isSavedUriBackInit())
            binding.previewViewId.imageCaptured.setImageURI(MyViewModel.savedUriBack)
        binding.previewViewId.imageCaptured.scaleType = ImageView.ScaleType.CENTER_CROP
    }

}