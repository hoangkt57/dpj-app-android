package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.database.DatabaseUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sonyged.hyperClass.model.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WorkoutCreateViewModel(application: Application, val data: Workout) :
    BaseViewModel(application) {

    val workout = MutableLiveData<Workout>()
    val uris = MutableLiveData<LinkedHashMap<String, Workout.Attachment>>()

    val attachmentDeleted = hashSetOf<String>()

    var date = System.currentTimeMillis()
    var time = Pair(0, 0)

    init {
        if (isEditing()) {
            workout.postValue(data)
        }
    }

    fun isEditing(): Boolean {
        return data.id.isNotEmpty()
    }

    fun extractTime(time: String): Pair<Int, Int> {
        try {
            val split = time.split(":")
            return Pair(split[0].toInt(), split[1].toInt())
        } catch (e: Exception) {
            Timber.e(e, "extractTime")
        }
        return Pair(0, 0)
    }

    fun removeFile(id: String) {
        Timber.d("removeFile - id: $id")
        try {
            val result = uris.value ?: LinkedHashMap()
            if(id.contains(":")) {
                result.remove(id)
            } else {
                attachmentDeleted.add(id)
            }
            uris.value = result
        } catch (e: Exception) {
            Timber.e(e, "removeFile")
        }
    }

    fun setFile(data: List<Uri>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val context = getApplication<Application>()
                val result = uris.value ?: LinkedHashMap()
                data.forEach { uri ->
                    context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                        Timber.d("setFile -  ${DatabaseUtils.dumpCursorToString(cursor)}")
                        Timber.d("setFile - count: ${cursor?.count}")
                        if (cursor == null || cursor.isClosed || cursor.count == 0) {
                            return@launch
                        }
                        cursor.moveToFirst()
                        val id =
                            cursor.getString(cursor.getColumnIndex("document_id"))
                        val name =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                        val mimeType =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
                        if (result.containsKey(id)) {
                            return@forEach
                        }
                        result[id] = Workout.Attachment(id, name, mimeType, uri.toString())
                    }
                    uris.postValue(result)
                }
            } catch (e: Exception) {
                Timber.e(e, "setFile")
            }
        }
    }
}