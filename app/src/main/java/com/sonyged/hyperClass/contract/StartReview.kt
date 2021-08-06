package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.ReviewActivity
import com.sonyged.hyperClass.constants.KEY_STUDENT_WORKOUT_ID
import com.sonyged.hyperClass.constants.KEY_STUDENT_WORKOUT_LIST_ID

class StartReview : ActivityResultContract<Pair<String, ArrayList<String>>, Boolean>() {

    override fun createIntent(context: Context, input: Pair<String, ArrayList<String>>): Intent {
        val intent = Intent(context, ReviewActivity::class.java)
        intent.putExtra(KEY_STUDENT_WORKOUT_ID, input.first)
        intent.putStringArrayListExtra(KEY_STUDENT_WORKOUT_LIST_ID, input.second)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}