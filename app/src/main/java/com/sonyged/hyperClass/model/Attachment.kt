package com.sonyged.hyperClass.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    val id: String,
    val filename: String,
    val contentType: String?,
    val url: String?
) : Parcelable