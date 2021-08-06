package com.sonyged.hyperClass.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.database.DatabaseUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.FileUpload
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.WorkoutCreateMutation
import com.sonyged.hyperClass.WorkoutUpdateMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.DATE_INVALID
import com.sonyged.hyperClass.constants.STATUS_LOADING
import com.sonyged.hyperClass.model.Attachment
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.type.WorkoutCreateInput
import com.sonyged.hyperClass.type.WorkoutUpdateInput
import com.sonyged.hyperClass.utils.formatServerTime
import com.sonyged.hyperClass.utils.getCurrentTimeUTC
import com.sonyged.hyperClass.utils.localTimeToUTC
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class WorkoutCreateViewModel(application: Application, val courseId: String) :
    BaseViewModel(application) {

    val workout = MutableLiveData<Workout>()
    val uris = MutableLiveData<LinkedHashMap<String, Attachment>>()

    val attachmentDeleted = hashSetOf<String>()

    var date = DATE_INVALID
    var time: Pair<Int, Int>? = null

    @DelicateCoroutinesApi
    override fun onCleared() {
        super.onCleared()

        deleteOldFiles()
    }

    fun isEditing(): Boolean {
        return courseId.isEmpty()
    }

    fun removeFile(id: String) {
        Timber.d("removeFile - id: $id")
        try {
            val result = uris.value ?: LinkedHashMap()
            if (id.contains(":")) {
                removeInternalFile(result[id])
                result.remove(id)
            } else {
                attachmentDeleted.add(id)
            }
            uris.value = result
        } catch (e: Exception) {
            Timber.e(e, "removeFile")
        }
    }

    private fun removeInternalFile(attachment: Attachment?) {
        Timber.d("removeInternalFile - attachment: $attachment")
        if (attachment?.path == null) {
            return
        }
        try {
            val file = File(attachment.path)
            file.deleteRecursively()
        } catch (e: Exception) {
            Timber.e(e, "removeInternalFile")
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
                        if (cursor == null || cursor.isClosed || cursor.count == 0) {
                            return@launch
                        }
                        cursor.moveToFirst()
                        val id = cursor.getString(cursor.getColumnIndex("document_id"))
                        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
                        if (result.containsKey(id)) {
                            return@forEach
                        }
                        val uploadFolder = File(context.filesDir, "upload")
                        val uploadSubFolder = File(uploadFolder, "${System.currentTimeMillis()}")
                        uploadSubFolder.mkdirs()
                        val upload = File(uploadSubFolder, name)
                        context.contentResolver.openInputStream(uri).use {
                            it?.copyTo(upload.outputStream())
                        }
                        result[id] = Attachment(id, DATE_INVALID, name, mimeType, uri.toString(), upload.absolutePath)
                        Timber.d("setFile - new attachment: ${result[id]}")
                    }
                    uris.postValue(result)
                }
            } catch (e: Exception) {
                Timber.e(e, "setFile")
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun createWorkout(name: String, content: String) {
        Timber.d("createWorkout")
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val context = getApplication<Application>()
                if (name.isEmpty()) {
                    sendErrorStatus(context.getString(R.string.title_required))
                    return@launch
                }
                val description = if (content.isNotEmpty()) {
                    Input.optional(content)
                } else {
                    Input.absent()
                }
                val currentTime = getCurrentTimeUTC()
                val dateTime = localTimeToUTC(date, time?.first ?: 0, time?.second ?: 0)
                val dateTimeText = formatServerTime(dateTime)
                if (date != DATE_INVALID && dateTime < currentTime + 60 * 1000) {
                    sendErrorStatus(context.getString(R.string.time_is_not_feature, dateTimeText))
                    return@launch
                }
                val timeServer = if (date != DATE_INVALID) {
                    Input.optional(dateTimeText as Any)
                } else {
                    Input.absent()
                }
                Timber.d(
                    "createWorkout - courseId: $courseId - name: $name - content: $content - date: $date - time: $time " +
                            "- dateTime: $dateTimeText - attachmentDeleted: $attachmentDeleted"
                )

                val deletedAttachmentIds = if (attachmentDeleted.isNotEmpty()) {
                    val list = arrayListOf<String>()
                    attachmentDeleted.forEach {
                        list.add(it)
                    }
                    Input.optional(list.toList())
                } else {
                    Input.absent()
                }

                val newFile = arrayListOf<Attachment>()
                uris.value?.forEach { (id, value) ->
                    if (id.contains(":")) {
                        newFile.add(value)
                    }
                }
                val attachments = if (newFile.isNotEmpty()) {
                    val list = arrayListOf<FileUpload>()
                    newFile.forEach {
                        list.add(FileUpload(it.contentType ?: "", it.path))
                    }
                    Input.optional(list.toList())
                } else {
                    Input.absent()
                }

                if (isEditing()) {
                    val workout = workout.value
                    if (workout == null) {
                        sendErrorStatus()
                        return@launch
                    }
                    val query = WorkoutUpdateMutation(
                        workout.id,
                        WorkoutUpdateInput(
                            Input.optional(name),
                            description,
                            timeServer,
                            attachments,
                            deletedAttachmentIds
                        )
                    )
                    val response = ApiUtils.getApolloClient().mutate(query).await()
                    Timber.d("createWorkout - isEditing - response: $response")
                    val errors = response.data?.workoutUpdate?.asWorkoutMutationFailure?.errors
                    if (errors.isNullOrEmpty()) {
                        sendSuccessStatus()
                        return@launch
                    }
                    sendErrorStatus(errors)
                    return@launch
                }

                val query = WorkoutCreateMutation(
                    courseId,
                    WorkoutCreateInput(
                        name,
                        description,
                        timeServer,
                        attachments,
                        Input.absent(),
                        Input.absent()
                    )
                )
                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("createWorkout - response: $response")
                val errors = response.data?.workoutCreate?.asWorkoutMutationFailure?.errors
                if (errors.isNullOrEmpty()) {
                    sendSuccessStatus()
                    return@launch
                }
                sendErrorStatus(errors)
            } catch (e: Exception) {
                Timber.e(e, "createWorkout")
                sendErrorStatus()
            }
        }
    }

    @DelicateCoroutinesApi
    private fun deleteOldFiles() {
        Timber.d("deleteOldFiles")
        GlobalScope.launch(Dispatchers.Default) {
            val context = getApplication<Application>()
            val folder = File(context.filesDir, "upload")
            val result = folder.deleteRecursively()
            Timber.d("deleteOldFiles - result: $result")
        }
    }
}