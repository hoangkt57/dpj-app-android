package com.sonyged.hyperClass.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(
    val id: String,
    val name: String,
    val type: String
) : Parcelable {

    companion object {
        fun empty(): Person {
            return Person("", "", "")
        }
    }
}