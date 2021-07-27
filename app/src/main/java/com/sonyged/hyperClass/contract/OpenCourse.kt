package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.CourseActivity
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.model.Course

class OpenCourse : ActivityResultContract<Course, Boolean>() {

    override fun createIntent(context: Context, input: Course): Intent {
        val intent = Intent(context, CourseActivity::class.java)
        intent.putExtra(KEY_COURSE, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}