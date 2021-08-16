package com.sonyged.hyperClass.glide

import com.bumptech.glide.load.model.GlideUrl

class CustomGlideUrl(url: String?, private val id: String) : GlideUrl(url) {

    override fun getCacheKey(): String {
        return id
    }

}