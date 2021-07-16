package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WorkoutCreateViewModel(application: Application) : BaseViewModel(application) {

    val fileName = MutableLiveData<String>()

    private var fileUri: Uri? = null

    fun removeFile() {
        fileUri = null
        fileName.postValue("")
    }

    fun setFile(uri: Uri) {
        fileUri = uri
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val context = getApplication<Application>()
                context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                    Timber.d("setFile - count: ${cursor?.count}")
                    if (cursor == null || cursor.isClosed || cursor.count == 0) {
                        return@launch
                    }
                    cursor.moveToFirst()
                    val name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                    fileName.postValue(name)
                }
            } catch (e: Exception) {
                Timber.e(e, "setFile")
            }
        }
    }
}