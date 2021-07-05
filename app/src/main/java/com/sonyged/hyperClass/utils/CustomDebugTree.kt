package com.sonyged.hyperClass.utils

import timber.log.Timber

class CustomDebugTree : Timber.DebugTree() {

    companion object {
        private const val TAG_PREFIX = "DPJ#"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, TAG_PREFIX + tag, message, t)
    }

}