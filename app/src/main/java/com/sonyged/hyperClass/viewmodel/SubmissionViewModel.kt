package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.database.DatabaseUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.FileUpload
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.BuildConfig
import com.sonyged.hyperClass.CreateStudentWorkoutMutation
import com.sonyged.hyperClass.SubmitAdditionalStudentWorkoutMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.DATE_INVALID
import com.sonyged.hyperClass.constants.STATUS_LOADING
import com.sonyged.hyperClass.constants.STATUS_SUCCESSFUL
import com.sonyged.hyperClass.model.Attachment
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.type.StudentWorkoutHandInInput
import com.sonyged.hyperClass.type.StudentWorkoutSubmitAdditionalInput
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class SubmissionViewModel(application: Application, val studentWorkoutId: String) : BaseViewModel(application) {

    val workout = MutableLiveData<Workout>()
    val attachments = MutableLiveData<List<Attachment>>()

    var imageUri: Uri? = null

    @DelicateCoroutinesApi
    override fun onCleared() {
        Timber.d("onCleared")
        super.onCleared()

        deleteOldImages()
    }

    fun initAttachments(workout: Workout) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = arrayListOf<Attachment>()
            workout.submissionFile.forEach {
                result.add(it)
            }
            attachments.postValue(result)
        }
    }

    fun pickUri(): Uri? {
        val context = getApplication<Application>()
        val folder = File(context.filesDir, "camera")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val name = "image_camera_${System.currentTimeMillis()}.jpg"
        val file = File(folder, name)
        imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)
        return imageUri
    }

    fun addCaptureImage() {
        imageUri?.let {
            addImage(arrayListOf(it))
        }
    }

    private fun isExistFile(id: String): Boolean {
        attachments.value?.forEach {
            if (id == it.id) {
                return true
            }
        }
        return false
    }

    fun addImage(uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.Default) {
            val context = getApplication<Application>()
            val result = arrayListOf<Attachment>()
            uris.forEach { uri ->
                if (uri.authority == "com.sonyged.dev.hyperClass.fileprovider") {
                    result.add(
                        Attachment(
                            uri.toString(),
                            DATE_INVALID,
                            "",
                            "image/jpeg",
                            uri.toString(),
                            context.filesDir.absolutePath + "/camera/" + uri.lastPathSegment
                        )
                    )
                } else {
                    context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                        Timber.d("addImages -  ${DatabaseUtils.dumpCursorToString(cursor)}")
                        if (cursor == null || cursor.isClosed || cursor.count == 0) {
                            return@launch
                        }
                        cursor.moveToFirst()
                        val id = cursor.getString(cursor.getColumnIndex("document_id"))
                        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
                        if (isExistFile(id)) {
                            return@forEach
                        }
                        val uploadFolder = File(context.filesDir, "upload")
                        val uploadSubFolder = File(uploadFolder, "${System.currentTimeMillis()}")
                        uploadSubFolder.mkdirs()
                        val upload = File(uploadSubFolder, name)
                        context.contentResolver.openInputStream(uri).use {
                            it?.copyTo(upload.outputStream())
                        }
                        result.add(Attachment(id, DATE_INVALID, name, mimeType, uri.toString(), upload.absolutePath))
                    }
                }
            }
            val list = attachments.value ?: arrayListOf()
            attachments.postValue(list.plus(result))
        }
    }

    fun deleteImage(attachment: Attachment) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = arrayListOf<Attachment>()
            val list = attachments.value ?: arrayListOf()
            list.forEach {
                if (it.id != attachment.id) {
                    result.add(it)
                }
            }
            attachments.postValue(result)
        }
    }

    fun createSubmission(answer: String) {
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            Timber.d("createSubmission - studentWorkoutId: $studentWorkoutId")
            try {
                val id = if (studentWorkoutId.isEmpty()) {
                    workout.value?.id
                } else {
                    studentWorkoutId
                }
                if (id.isNullOrEmpty()) {
                    sendErrorStatus("")
                    return@launch
                }
                val attachmentList = attachments.value ?: arrayListOf()
                val fileUpload = arrayListOf<FileUpload>()
                val deletedIds = arrayListOf<String>()
                workout.value?.submissionFile?.forEach {
                    deletedIds.add(it.id)
                }
                attachmentList.forEach {
                    if (deletedIds.contains(it.id)) {
                        deletedIds.remove(it.id)
                    }
                    if (it.path != null) {
                        fileUpload.add(FileUpload(it.contentType ?: "", it.path))
                    }
                }
                Timber.d("createSubmission - deletedIds: $deletedIds")
                if (studentWorkoutId.isEmpty()) {
                    val query = CreateStudentWorkoutMutation(
                        id,
                        StudentWorkoutHandInInput(
                            Input.optional(answer),
                            Input.optional(fileUpload),
                            Input.optional(false)
                        )
                    )
                    val response = ApiUtils.getApolloClient().mutate(query).await()
                    Timber.d("createSubmission - response:$response")
                    status.postValue(Status(STATUS_SUCCESSFUL))
                    return@launch
                }
                val query = SubmitAdditionalStudentWorkoutMutation(
                    studentWorkoutId,
                    StudentWorkoutSubmitAdditionalInput(
                        Input.optional(answer),
                        Input.optional(fileUpload),
                        Input.optional(deletedIds)
                    )
                )
                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("createSubmission - response:$response")
                status.postValue(Status(STATUS_SUCCESSFUL))
            } catch (e: Exception) {
                Timber.e(e, "createSubmission")
            }
        }
    }

    @DelicateCoroutinesApi
    private fun deleteOldImages() {
        Timber.d("deleteOldImages")
        GlobalScope.launch(Dispatchers.Default) {
            val context = getApplication<Application>()
            val folder = File(context.filesDir, "camera")
            val updateFolder = File(context.filesDir, "upload")
            val result = folder.deleteRecursively() || updateFolder.deleteRecursively()
            Timber.d("deleteOldImages - result: $result")
        }
    }
}