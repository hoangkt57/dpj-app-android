package com.sonyged.hyperClass.views

import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption

fun getCourseCoverImage(image: DefaultCourseCoverImageOption?): Int {
    return when (image) {
        DefaultCourseCoverImageOption._1 -> {
            R.drawable.ic_subject_file
        }
        else -> {
            -1
        }
    }
}