package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageCourseDetailQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.CourseDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CourseDetailViewModel(application: Application, val courseId: String) : BaseViewModel(application) {

    val courseDetail = MutableLiveData<CourseDetail>()

    init {
        loadCourseDetail()
    }

    private fun loadCourseDetail() {
        Timber.d("loadCourseDetail")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val courseResponse = ApiUtils.getApolloClient().query(PageCourseDetailQuery(courseId)).await()
                Timber.d("loadCourseDetail - courseResponse: $courseResponse")

                courseResponse.data?.node?.asCourse?.let {
                    val tags = arrayListOf<String>()
                    it.schoolTagsConnection?.edges?.forEach { tagEdge ->
                        tagEdge?.node?.let {
                            tags.add(it.name)
                        }
                    }
                    courseDetail.postValue(
                        CourseDetail(
                            it.id,
                            it.name ?: "",
                            TextUtils.join(", ", tags),
                            it.teacher.name ?: "",
                            "",
                            it.autoCreateLessonWorkout,
                            0
                        )
                    )
                }


            } catch (e: Exception) {
                Timber.e(e, "loadCourseDetail")
            }
        }
    }
}