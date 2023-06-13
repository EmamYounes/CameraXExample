package com.example.cameraxexample.callbacks

import com.example.cameraxexample.model.GalleryModel

interface ClickImageCallback {
    fun onDeleteImageClick(galleryModel: GalleryModel)
}