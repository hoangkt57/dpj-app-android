package com.sonyged.hyperClass.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    val id: String,
    val createAt: Long,
    val filename: String,
    val contentType: String?,
    val url: String?,
    val path: String?
) : Parcelable {

    constructor(id: String, createAt: Long, filename: String, contentType: String?, url: String?) : this(
        id,
        createAt,
        filename,
        contentType,
        url,
        null
    )

    class DiffCallback : DiffUtil.ItemCallback<Attachment>() {
        override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Attachment, newItem: Attachment): Boolean {
            return oldItem == newItem
        }
    }
}