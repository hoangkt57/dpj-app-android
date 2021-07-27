package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.StudentListActivity
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.constants.KEY_NEW_STUDENT_COUNT
import com.sonyged.hyperClass.model.Course

class OpenStudentList : ActivityResultContract<Course, Int>() {

    override fun createIntent(context: Context, input: Course): Intent {
        val intent = Intent(context, StudentListActivity::class.java)
        intent.putExtra(KEY_COURSE, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        if (resultCode == Activity.RESULT_OK) {
            return intent?.getIntExtra(KEY_NEW_STUDENT_COUNT, -1) ?: -1
        }
        return -1
    }
}