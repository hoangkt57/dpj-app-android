package com.sonyged.hyperClass.views

import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption

fun getCourseCoverImageList(): List<DefaultCourseCoverImageOption> {
    return arrayListOf(
        DefaultCourseCoverImageOption._1,
        DefaultCourseCoverImageOption._2,
        DefaultCourseCoverImageOption._3,
        DefaultCourseCoverImageOption._4,
        DefaultCourseCoverImageOption._5,
        DefaultCourseCoverImageOption._6,
        DefaultCourseCoverImageOption._7,
        DefaultCourseCoverImageOption._8,
        DefaultCourseCoverImageOption._9,
        DefaultCourseCoverImageOption._10,
        DefaultCourseCoverImageOption._11,
        DefaultCourseCoverImageOption._12,
        DefaultCourseCoverImageOption._13
    )
}

fun getCourseCoverImage(image: DefaultCourseCoverImageOption?): Int {
    return when (image) {
        DefaultCourseCoverImageOption._1 -> {
            R.drawable.ic_subject_file
        }
        DefaultCourseCoverImageOption._2 -> {
            R.drawable.ic_subject_book
        }
        DefaultCourseCoverImageOption._3 -> {
            R.drawable.ic_subject_write
        }
        DefaultCourseCoverImageOption._4 -> {
            R.drawable.ic_subject_note
        }
        DefaultCourseCoverImageOption._5 -> {
            R.drawable.ic_subject_check
        }
        DefaultCourseCoverImageOption._6 -> {
            R.drawable.ic_subject_document
        }
        DefaultCourseCoverImageOption._7 -> {
            R.drawable.ic_subject_test
        }
        DefaultCourseCoverImageOption._8 -> {
            R.drawable.ic_subject_japanese
        }
        DefaultCourseCoverImageOption._9 -> {
            R.drawable.ic_subject_english
        }
        DefaultCourseCoverImageOption._10 -> {
            R.drawable.ic_subject_arithmetic
        }
        DefaultCourseCoverImageOption._11 -> {
            R.drawable.ic_subject_math
        }
        DefaultCourseCoverImageOption._12 -> {
            R.drawable.ic_subject_science
        }
        DefaultCourseCoverImageOption._13 -> {
            R.drawable.ic_subject_social
        }

        else -> {
            0
        }
    }
}