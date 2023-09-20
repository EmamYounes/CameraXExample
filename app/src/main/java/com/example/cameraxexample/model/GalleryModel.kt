package com.example.cameraxexample.model

import android.net.Uri

data class GalleryModel(
    var uri: Uri,
    var isChecked: Boolean = false,
    val imageName: String,
    var isFront: Boolean = false,
    var uploadedSuccessfully: Boolean = false,
    val docBase64: String?,
    val docTypeCode: String?,
) {
    var docId: String? = ""
    var isUploaded = false
}
