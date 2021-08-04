package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageCourseQuery
import com.sonyged.hyperClass.SegLessonsQuery
import com.sonyged.hyperClass.SegWorkoutsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.DATE_INVALID
import com.sonyged.hyperClass.model.*
import com.sonyged.hyperClass.type.*
import com.sonyged.hyperClass.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class CourseViewModel(application: Application, val courseId: String) : BaseViewModel(application) {

    val course = MutableLiveData<Course>()
    val lessonDateRange = MutableLiveData<Pair<Long, Long>>()
    val lessons = lessonDateRange.switchMap { dateRange ->
        liveData(Dispatchers.Default) {
            val from = formatDate(dateRange.first)
            val until = formatDate(dateRange.second)
            val result = loadLessons(from, until)
            val temp = MutableLiveData<List<Exercise>>(result)
            emitSource(temp)
        }
    }
    val workoutDateRange = MutableLiveData<Pair<Long, Long>>()
    val workouts = workoutDateRange.switchMap { dateRange ->
        liveData(Dispatchers.Default) {
            val from = formatDate(dateRange.first)
            val until = formatDate(dateRange.second)
            val result = loadWorkouts(from, until)
            val temp = MutableLiveData<List<Exercise>>(result)
            emitSource(temp)
        }
    }

    init {
        val time = range7DayFromCurrent()
        lessonDateRange.postValue(Pair(time.first, time.second))
        workoutDateRange.postValue(Pair(time.first, time.second))
        loadCourse()
    }

    fun loadCourse() {
        Timber.d("loadCourse")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = PageCourseQuery(courseId)
                val response = ApiUtils.getApolloClient().query(query).await()
//                Timber.d("loadCourse - response : $response")
                response.data?.node?.asCourse?.let {
                    val result = Course(
                        it.id,
                        it.name ?: "",
                        it.coverImage.asDefaultCourseCoverImage?.value ?: DefaultCourseCoverImageOption.UNKNOWN__,
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
                        it.studentsConnection?.totalCount ?: 0,
                        arrayListOf()
                    )
                    course.postValue(result)
                }
            } catch (e: Exception) {
                Timber.e(e, "loadCourse")
            }
        }
    }

    fun loadLessons() {
        lessonDateRange.value?.let {
            val data = Pair(it.first, it.second)
            lessonDateRange.postValue(data)
        }
    }

    fun loadWorkouts() {
        workoutDateRange.value?.let {
            val data = Pair(it.first, it.second)
            workoutDateRange.postValue(data)
        }
    }

    private suspend fun loadLessons(from: String, until: String): ArrayList<Exercise> {
        Timber.d("loadLessons - from: $from - until: $until")
        val result = arrayListOf<Exercise>()
        try {
            val lessonsQuery = SegLessonsQuery(
                courseId,
                filter = Input.optional(
                    LessonFilter(
                        beginAtBetween = Input.optional(
                            DateRange(
                                Input.optional(from),
                                Input.optional(until)
                            )
                        )
                    )
                )
            )
            val lessonResponse = ApiUtils.getApolloClient().query(lessonsQuery).await()
//                Timber.d("loadData - data : ${lessonResponse}")
            val courseTitle = lessonResponse.data?.node?.asCourse?.name ?: ""
            lessonResponse.data?.node?.asCourse?.lessonsConnection?.edges?.forEach { edge ->
                edge?.node?.let {
                    val teacherName = it.teacher.name ?: ""
                    result.add(
                        Exercise(
                            it.id,
                            it.name,
                            formatDateTime(it.beginAt as String?) + " - " + formatTime(formatDateTimeToLong(it.endAt as String?)),
                            UserEventFilterType.LESSON,
                            teacherName,
                            courseTitle,
                            null,
                            it.kickUrl
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "loadLessons")
        }
        return result
    }

    private suspend fun loadWorkouts(from: String, until: String): ArrayList<Exercise> {
        Timber.d("loadWorkouts")
        val result = arrayListOf<Exercise>()
        try {
            val context = getApplication<Application>()
            val query = SegWorkoutsQuery(
                courseId,
                isTeacher = isTeacher(),
                filter = Input.optional(
                    WorkoutFilter(
                        dueDateBetween = Input.optional(
                            DateRange(
                                Input.optional(from),
                                Input.optional(until)
                            )
                        )
                    )
                )
            )
            val response = ApiUtils.getApolloClient().query(query).await()
            val courseTitle = response.data?.node?.asCourse?.name ?: ""
            response.data?.node?.asCourse?.workoutsConnection?.edges?.forEach { edge ->
                edge?.node?.let {
                    val teacherName = it.course.teacher.name ?: ""
                    val files = arrayListOf<Attachment>()
                    it.studentWorkout?.attachments?.forEach { attachment ->
                        files.add(
                            Attachment(
                                attachment.id,
                                DATE_INVALID,
                                attachment.filename,
                                attachment.__typename,
                                attachment.url
                            )
                        )
                    }
                    var submittedCount = 0
                    it.studentWorkouts?.forEach { studentWorkout ->
                        if (studentWorkout.status == WorkoutStatus.SUBMITTED) {
                            submittedCount++
                        }
                    }

                    val status = if (isTeacher()) {
                        if (submittedCount == 0) {
                            WorkoutStatus.UNKNOWN__
                        } else {
                            TeacherStatus.VERIFY
                        }
                    } else {
                        it.studentWorkout?.status ?: WorkoutStatus.NONE
                    }
                    val temp = StatusResource.getStatus(context, status)
                    val statusResource = if (temp != null && status == TeacherStatus.VERIFY) {
                        temp.copy(text = temp.text + " " + submittedCount)
                    } else {
                        temp
                    }
                    result.add(
                        Exercise(
                            it.id,
                            it.title,
                            formatDateTime(it.dueDate as String?),
                            UserEventFilterType.WORKOUT,
                            teacherName,
                            courseTitle,
                            statusResource,
                            null,
                            it.studentWorkout?.description,
                            files
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "loadWorkouts")
        }
        return result
    }
}