package com.sonyged.hyperClass.observer

import android.os.Bundle

interface Observer {

    fun onEvent(event: Int, data: Bundle?)
}