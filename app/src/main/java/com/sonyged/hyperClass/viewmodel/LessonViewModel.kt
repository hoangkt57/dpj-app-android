package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.SegExplanationQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.utils.formatDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LessonViewModel(application: Application, val lessonId: String) : BaseViewModel(application) {

    val lesson = MutableLiveData<Lesson>()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        Timber.d("loadLesson")
        viewModelScope.launch(Dispatchers.Default) {
            try {

                val lessonResponse = ApiUtils.getApolloClient().query(SegExplanationQuery(lessonId)).await()

                val id = lessonResponse.data?.node?.asLesson?.id ?: ""
                val name = lessonResponse.data?.node?.asLesson?.name ?: ""
                val courseName = lessonResponse.data?.node?.asLesson?.course?.name ?: ""
                val teacher = lessonResponse.data?.node?.asLesson?.teacher?.name ?: ""
                val date = formatDateTime(lessonResponse.data?.node?.asLesson?.beginAt as String?)
                val studentCount = lessonResponse.data?.node?.asLesson?.students?.size ?: 0
                val kickUrl = lessonResponse.data?.node?.asLesson?.kickUrl

                lesson.postValue(Lesson(id, name, courseName, date, teacher, studentCount, kickUrl))

            } catch (e: Exception) {
                Timber.e(e)
            }
        }

    }

}