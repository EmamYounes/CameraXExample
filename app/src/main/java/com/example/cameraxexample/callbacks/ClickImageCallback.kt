package com.example.cameraxexample.callbacks

import com.example.cameraxexample.model.GalleryModel

interface ClickImageCallback {
    fun onSelectedImage(galleryModel: GalleryModel)
}