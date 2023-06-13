package com.example.cameraxexample.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cameraxexample.model.GalleryModel

object MyViewModel : ViewModel() {


    var imagesList: MutableList<GalleryModel> = mutableListOf()


}