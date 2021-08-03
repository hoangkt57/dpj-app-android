package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.ExerciseActivity
import com.sonyged.hyperClass.constants.KEY_ID
import com.sonyged.hyperClass.constants.KEY_LESSON
import com.sonyged.hyperClass.model.Exercise

class OpenWorkout : ActivityResultContract<Exercise, Boolean>() {
    override fun createIntent(context: Context, input: Exercise): Intent {
        val intent = Intent(context, ExerciseActivity::class.java)
        intent.putExtra(KEY_ID, input.id)
        intent.putExtra(KEY_LESSON, false)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}