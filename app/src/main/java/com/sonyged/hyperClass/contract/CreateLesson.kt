package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.LessonCreateActivity
import com.sonyged.hyperClass.constants.KEY_COURSE_ID
import com.sonyged.hyperClass.constants.KEY_LESSON

class CreateLesson : ActivityResultContract<CreateLessonInput, Boolean>() {

    override fun createIntent(context: Context, input: CreateLessonInput): Intent {
        val intent = Intent(context, LessonCreateActivity::class.java)
        intent.putExtra(KEY_COURSE_ID, input.courseId)
        intent.putExtra(KEY_LESSON, input.lesson)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}