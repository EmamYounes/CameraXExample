package com.example.cameraxexample.view.test

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.egabi.digitalsharjah_services.ui.RF_rafied.shared_components.view.camera.ScanningFragment
import com.example.cameraxexample.R
import com.example.cameraxexample.enums.TypeEnum
import com.example.cameraxexample.model.GalleryModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TestScanningFragment : ScanningFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController =
            activity?.findNavController(R.id.nav_host_fragment_content_main)
        navController?.currentDestination?.id?.let { }

    }

    override fun imagesList(): MutableList<GalleryModel> {
        return mutableListOf()
    }

    override fun isFirstScreen(): Boolean {
        return findNavController().currentDestination?.id == R.id.FrontScanningFragment
    }

    override fun firstPreviewScreenID(): Int {
        return R.id.previewScanningFragment
    }

    override fun secondPreviewScreenID(): Int {
        return R.id.previewScanningFragment2
    }

    override fun firstScreenTitle(): String {
        return "First Screen"
    }

    override fun secondScreenTitle(): String {
        return "Second Screen"
    }

    override fun firstPhotoName(): String {
        return "front"
    }

    override fun secondPhotoName(): String {
        return "back"
    }

    override fun docTypeCodeEnumFront(): TypeEnum {
        return TypeEnum.FRONT
    }

    override fun docTypeCodeEnumBack(): TypeEnum {
        return TypeEnum.BACK
    }
}