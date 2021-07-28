package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageCourseDetailQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.CourseDetail
import com.sonyged.hyperClass.model.Person
import com.sonyged.hyperClass.model.Tag
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption
import com.sonyged.hyperClass.utils.formatDateTimeToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CourseDetailViewModel(application: Application, private val courseId: String, private val teacherId: String) :
    BaseViewModel(application) {

    val courseDetail = MutableLiveData<CourseDetail>()
    val allTags = arrayListOf<Tag>()

    init {
        loadCourseDetail()
    }

    fun isOwner(): Boolean {
        return teacherId == sharedPref.getUserId()
    }

    fun loadCourseDetail() {
        Timber.d("loadCourseDetail")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val courseResponse = ApiUtils.getApolloClient().query(PageCourseDetailQuery(courseId)).await()
                Timber.d("loadCourseDetail - courseResponse: $courseResponse")

                courseResponse.data?.node?.asCourse?.let {
                    val tags = arrayListOf<Tag>()
                    it.schoolTagsConnection?.edges?.forEach { tagEdge ->
                        tagEdge?.node?.let { tag ->
                            tags.add(Tag(tag.id, tag.name))
                        }
                    }
                    allTags.clear()
                    it.school.schoolTagsConnection?.edges?.forEach { tagEdge ->
                        tagEdge?.node?.let { tag ->
                            allTags.add(Tag(tag.id, tag.name))
                        }
                    }
                    courseDetail.postValue(
                        CourseDetail(
                            it.id,
                            it.name ?: "",
                            tags,
                            Person(
                                it.teacher.id,
                                it.teacher.name ?: "",
                                it.teacher.__typename
                            ),
                            Person(
                                it.assistant?.id ?: "",
                                it.assistant?.name ?: "",
                                it.assistant?.__typename ?: ""
                            ),
                            formatDateTimeToLong(it.expiredAt.toString()),
                            it.autoCreateLessonWorkout,
                            it.coverImage.asDefaultCourseCoverImage?.value ?: DefaultCourseCoverImageOption.UNKNOWN__
                        )
                    )
                }


            } catch (e: Exception) {
                Timber.e(e, "loadCourseDetail")
            }
        }
    }
}