package com.sonyged.hyperClass.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sonyged.hyperClass.activity.TrophyActivity
import com.sonyged.hyperClass.constants.KEY_COURSE_ID

class OpenTrophy : ActivityResultContract<String, Boolean>() {

    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(context, TrophyActivity::class.java)
        intent.putExtra(KEY_COURSE_ID, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}