package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.StudentActivity
import com.sonyged.hyperClass.constants.KEY_STUDENT_ID
import com.sonyged.hyperClass.constants.KEY_TITLE
import com.sonyged.hyperClass.model.Student

class OpenStudent : ActivityResultContract<Student, Boolean>() {

    override fun createIntent(context: Context, input: Student): Intent {
        val intent = Intent(context, StudentActivity::class.java)
        intent.putExtra(KEY_STUDENT_ID, input.id)
        intent.putExtra(KEY_TITLE, input.name)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}