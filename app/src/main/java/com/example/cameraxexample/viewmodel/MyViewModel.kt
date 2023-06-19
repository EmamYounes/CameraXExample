package com.example.cameraxexample.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.cameraxexample.model.GalleryModel

object MyViewModel : ViewModel() {


    var imagesList: MutableList<GalleryModel> = mutableListOf()

    lateinit var savedUriFront: Uri
    lateinit var savedUriBack: Uri

    fun isSavedUriBackInit(): Boolean {
        return this::savedUriBack.isInitialized
    }

}