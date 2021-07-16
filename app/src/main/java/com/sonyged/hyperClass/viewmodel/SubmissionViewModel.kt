package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sonyged.hyperClass.BuildConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class SubmissionViewModel(application: Application, val studentWorkoutId: String) : BaseViewModel(application) {

    var imageUri: Uri? = null

    val images = MutableLiveData<List<Uri>>()

    @DelicateCoroutinesApi
    override fun onCleared() {
        Timber.d("onCleared")
        super.onCleared()

        deleteOldImages()
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
            addImage(it)
        }
    }

    fun addImage(uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.Default) {
            val list = images.value ?: arrayListOf()
            images.postValue(list.plus(uris))
        }
    }

    fun addImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            val list = images.value ?: arrayListOf()
            images.postValue(list.plus(uri))
        }
    }

    fun deleteImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            val list = images.value ?: arrayListOf()
            images.postValue(list.minus(uri))
        }
    }

    @DelicateCoroutinesApi
    private fun deleteOldImages() {
        Timber.d("deleteOldImages")
        GlobalScope.launch(Dispatchers.Default) {
            val context = getApplication<Application>()
            val folder = File(context.filesDir, "camera")
            val result = folder.deleteRecursively()
            Timber.d("deleteOldImages - result: $result")
        }
    }
}