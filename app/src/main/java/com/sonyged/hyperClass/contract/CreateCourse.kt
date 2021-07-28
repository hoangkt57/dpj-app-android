package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.CourseCreateActivity
import com.sonyged.hyperClass.constants.KEY_COURSE_DETAIL
import com.sonyged.hyperClass.constants.KEY_TAG
import com.sonyged.hyperClass.model.CourseDetail
import com.sonyged.hyperClass.model.Tag

class CreateCourse : ActivityResultContract<Pair<CourseDetail?, ArrayList<Tag>>, Boolean>() {

    override fun createIntent(context: Context, input: Pair<CourseDetail?, ArrayList<Tag>>): Intent {
        val intent = Intent(context, CourseCreateActivity::class.java)
        intent.putExtra(KEY_COURSE_DETAIL, input.first)
        intent.putParcelableArrayListExtra(KEY_TAG, input.second)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}