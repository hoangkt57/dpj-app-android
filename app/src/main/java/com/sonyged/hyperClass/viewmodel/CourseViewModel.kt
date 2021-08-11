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
import com.sonyged.hyperClass.constants.DEFAULT_PAGE_ID
import com.sonyged.hyperClass.model.*
import com.sonyged.hyperClass.type.*
import com.sonyged.hyperClass.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class CourseViewModel(application: Application, val courseId: String) : BaseViewModel(application) {

    val course = MutableLiveData<Course>()
    val lessonFilter = MutableLiveData<ExerciseFilter>()
    val lessons = lessonFilter.switchMap { filter ->
        liveData(Dispatchers.Default) {
            if (filter.dateRange == null) {
                emitSource(MutableLiveData())
                return@liveData
            }
            val from = formatDate(filter.dateRange.first)
            val until = formatDate(filter.dateRange.second)
            val result = loadLessons(filter.page?.after, from, until)
            emitSource(getDataSource(true, filter.page?.after, result))
        }
    }
    val workoutFilter = MutableLiveData<ExerciseFilter>()
    val workouts = workoutFilter.switchMap { filter ->
        liveData(Dispatchers.Default) {
            if (filter.dateRange == null) {
                emitSource(MutableLiveData())
                return@liveData
            }
            val from = formatDate(filter.dateRange.first)
            val until = formatDate(filter.dateRange.second)
            val result = loadWorkouts(filter.page?.after, from, until)
            emitSource(getDataSource(false, filter.page?.after, result))
        }
    }

    init {
        val time = range7DayFromCurrent()
        lessonFilter.postValue(ExerciseFilter(Pair(time.first, time.second), null, null))
        workoutFilter.postValue(ExerciseFilter(Pair(time.first, time.second), null, null))
        loadCourse()
    }

    private fun getDataSource(isLesson: Boolean, after: String?, result: List<BaseItem>): MutableLiveData<List<BaseItem>> {
        return if (after.isNullOrEmpty()) {
            MutableLiveData<List<BaseItem>>(result)
        } else {
            val list = arrayListOf<BaseItem>()
            val oldList = if (isLesson) getLessonData() else getWorkoutData()
            list.addAll(oldList)
            if (list.isNotEmpty()) {
                val lastItem = list[list.size - 1]
                if (lastItem is Page) {
                    list.remove(lastItem)
                }
            }
            list.addAll(result)
            MutableLiveData<List<BaseItem>>(list)
        }
    }

    fun setLessonDateRange(dateRange: Pair<Long, Long>) {
        val data = lessonFilter.value ?: return
        lessonFilter.postValue(data.copy(dateRange = dateRange, page = null))
    }

    fun setWorkoutDateRange(dateRange: Pair<Long, Long>) {
        val data = workoutFilter.value ?: return
        workoutFilter.postValue(data.copy(dateRange = dateRange, page = null))
    }

    private fun getLessonData(): List<BaseItem> {
        return lessons.value ?: arrayListOf()
    }

    private fun getWorkoutData(): List<BaseItem> {
        return workouts.value ?: arrayListOf()
    }

    fun loadMoreLessons() {
        val list = lessons.value ?: return
        val item = list[list.size - 1]
        if (item is Page) {
            val data = lessonFilter.value ?: return
            lessonFilter.postValue(data.copy(page = item))
        }
    }

    fun loadMoreWorkouts() {
        val list = workouts.value ?: return
        val item = list[list.size - 1]
        if (item is Page) {
            val data = workoutFilter.value ?: return
            workoutFilter.postValue(data.copy(page = item))
        }
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
        lessonFilter.value?.let {
            lessonFilter.postValue(it)
        }
    }

    fun loadWorkouts() {
        workoutFilter.value?.let {
            workoutFilter.postValue(it)
        }
    }

    private suspend fun loadLessons(after: String?, from: String, until: String): ArrayList<BaseItem> {
        Timber.d("loadLessons - after: $after - from: $from - until: $until")
        val result = arrayListOf<BaseItem>()
        try {
            val context = getApplication<Application>()
            val afterInput = if (!after.isNullOrEmpty()) {
                Input.optional(after)
            } else {
                Input.absent()
            }
            val lessonsQuery = SegLessonsQuery(
                courseId,
                afterInput,
                Input.optional(
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
            val response = ApiUtils.getApolloClient().query(lessonsQuery).await()
//                Timber.d("loadData - data : ${lessonResponse}")
            val courseTitle = response.data?.node?.asCourse?.name ?: ""
            response.data?.node?.asCourse?.lessonsConnection?.edges?.forEach { edge ->
                edge?.node?.let {
                    val teacherName = it.teacher.name ?: ""
                    val beginAt = formatDateTimeToLong(it.beginAt as String?)
                    val endAt = formatDateTimeToLong(it.endAt as String?)
                    val current = System.currentTimeMillis()
                    val status = if (current in (beginAt + 1) until endAt) {
                        StatusResource.getStatus(context, LessonStatus.IN_PROGRESS)
                    } else {
                        null
                    }
                    result.add(
                        Exercise(
                            it.id,
                            it.name,
                            formatDateTime(it.beginAt as String?) + " - " + formatTime(formatDateTimeToLong(it.endAt as String?)),
                            UserEventFilterType.LESSON,
                            teacherName,
                            courseTitle,
                            status,
                            it.kickUrl
                        )
                    )
                }
            }
            response.data?.node?.asCourse?.lessonsConnection?.pageInfo?.let {
                if (it.hasNextPage) {
                    result.add(Page(DEFAULT_PAGE_ID, it.startCursor, it.endCursor))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "loadLessons")
        }
        return result
    }

    private suspend fun loadWorkouts(after: String?, from: String, until: String): ArrayList<BaseItem> {
        Timber.d("loadWorkouts")
        val result = arrayListOf<BaseItem>()
        try {
            val context = getApplication<Application>()
            val afterInput = if (!after.isNullOrEmpty()) {
                Input.optional(after)
            } else {
                Input.absent()
            }
            val query = SegWorkoutsQuery(
                courseId,
                afterInput,
                isTeacher(),
                Input.optional(
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
                                attachment.contentType,
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
                            null
                        } else {
                            TeacherStatus.VERIFY
                        }
                    } else {
                        it.studentWorkout?.status
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
            response.data?.node?.asCourse?.workoutsConnection?.pageInfo?.let {
                if (it.hasNextPage) {
                    result.add(Page(DEFAULT_PAGE_ID, it.startCursor, it.endCursor))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "loadWorkouts")
        }
        return result
    }
}