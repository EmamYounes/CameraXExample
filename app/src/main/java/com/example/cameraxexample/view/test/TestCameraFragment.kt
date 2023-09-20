package com.example.cameraxexample.view.test

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.cameraxexample.R
import com.example.cameraxexample.enums.TypeEnum
import com.example.cameraxexample.model.GalleryModel
import com.example.cameraxexample.view.CameraFragment

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TestCameraFragment : CameraFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = activity?.findNavController(R.id.nav_host_fragment_content_main)
        navController?.currentDestination?.id?.let { }
    }

    override fun setImagesDamageCar(list: MutableList<GalleryModel>) {
    }

    override fun imagesDamageCar(): MutableList<GalleryModel> {
        return mutableListOf()
    }

    override fun docTypeCodeEnum(): TypeEnum {
        return TypeEnum.CAMERA
    }
}