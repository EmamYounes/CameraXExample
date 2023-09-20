package com.example.cameraxexample.view.test

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cameraxexample.R
import com.example.cameraxexample.model.GalleryModel
import com.example.cameraxexample.view.PreviewScanningFragment

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TestPreviewScanningFragment : PreviewScanningFragment() {

    override fun imagesList(): MutableList<GalleryModel> {
        return mutableListOf()
    }

    override fun handleConfirmBtn() {
        if (isFirstScreen()) {
            findNavController().navigate(R.id.BackScanningFragment)
        } else {
        }
    }


    override fun isFirstScreen(): Boolean {
        return findNavController().currentDestination?.id == R.id.previewScanningFragment
    }

    override fun screenTitle(): String {
        return if (isFirstScreen()) {
            "First Screen"
        } else {
            "Second Screen"
        }
    }

}