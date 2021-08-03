package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.SubmissionActivity
import com.sonyged.hyperClass.constants.KEY_STUDENT_WORKOUT_ID
import com.sonyged.hyperClass.constants.KEY_WORKOUT
import com.sonyged.hyperClass.model.Workout

class OpenSubmissionWorkout : ActivityResultContract<Workout, Boolean>() {
    override fun createIntent(context: Context, input: Workout): Intent {
        val intent = Intent(context, SubmissionActivity::class.java)
        intent.putExtra(KEY_STUDENT_WORKOUT_ID, input.studentWorkoutId)
        intent.putExtra(KEY_WORKOUT, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}