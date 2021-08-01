package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.WorkoutCreateActivity
import com.sonyged.hyperClass.constants.KEY_COURSE_ID
import com.sonyged.hyperClass.constants.KEY_WORKOUT

class CreateWorkout : ActivityResultContract<CreateWorkoutInput, Boolean>() {

    override fun createIntent(context: Context, input: CreateWorkoutInput): Intent {
        val intent = Intent(context, WorkoutCreateActivity::class.java)
        intent.putExtra(KEY_COURSE_ID, input.courseId)
        intent.putExtra(KEY_WORKOUT, input.workout)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}