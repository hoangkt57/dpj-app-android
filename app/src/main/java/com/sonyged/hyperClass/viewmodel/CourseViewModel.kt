package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageCourseQuery
import com.sonyged.hyperClass.SegLessonsQuery
import com.sonyged.hyperClass.SegWorkoutsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.model.Person
import com.sonyged.hyperClass.type.*
import com.sonyged.hyperClass.utils.formatDate
import com.sonyged.hyperClass.utils.formatDateTime
import com.sonyged.hyperClass.utils.range7DayFromCurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CourseViewModel(application: Application, val courseId: String) : BaseViewModel(application) {

    val lessons = MutableLiveData<List<Exercise>>()

    val workouts = MutableLiveData<List<Exercise>>()

    val course = MutableLiveData<Course>()

    init {
        loadCourse()
        loadLessons()
        loadWorkouts()
    }

    fun loadCourse() {
        Timber.d("loadCourse")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = PageCourseQuery(courseId)
                val response = ApiUtils.getApolloClient().query(query).await()
                Timber.d("loadCourse - response : $response")

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

    private fun loadLessons() {
        Timber.d("loadLessons")

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val time = range7DayFromCurrent()
                val startTime = formatDate(time.first)
                val endTime = formatDate(time.second)
                val lessonsQuery = SegLessonsQuery(
                    courseId,
                    filter = Input.optional(
                        LessonFilter(
                            beginAtBetween = Input.optional(
                                DateRange(
                                    Input.optional(startTime),
                                    Input.optional(endTime)
                                )
                            )
                        )
                    )
                )

                val lessonResponse = ApiUtils.getApolloClient().query(lessonsQuery).await()

//                Timber.d("loadData - data : ${lessonResponse}")

                val result = arrayListOf<Exercise>()
                val courseTitle = lessonResponse.data?.node?.asCourse?.name ?: ""
                lessonResponse.data?.node?.asCourse?.lessonsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val teacherName = it.teacher.name ?: ""

                        result.add(
                            Exercise(
                                it.id,
                                it.name,
                                formatDateTime(it.beginAt as String?),
                                UserEventFilterType.LESSON,
                                teacherName,
                                courseTitle,
                                LessonStatus.UNKNOWN__,
                                it.kickUrl
                            )
                        )
                    }
                }

                lessons.postValue(result)

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun loadWorkouts() {
        Timber.d("loadWorkouts")

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val time = range7DayFromCurrent()
                val startTime = formatDate(time.first)
                val endTime = formatDate(time.second)
                val workoutsQuery = SegWorkoutsQuery(
                    courseId,
                    isTeacher = true,
                    filter = Input.optional(
                        WorkoutFilter(
                            dueDateBetween = Input.optional(
                                DateRange(
                                    Input.optional(startTime),
                                    Input.optional(endTime)
                                )
                            )
                        )
                    )
                )

                val workoutResponse = ApiUtils.getApolloClient().query(workoutsQuery).await()

                val result = arrayListOf<Exercise>()
                val courseTitle = workoutResponse.data?.node?.asCourse?.name ?: ""
                workoutResponse.data?.node?.asCourse?.workoutsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val teacherName = it.course.teacher.name ?: ""
                        val status = it.studentWorkout?.status ?: WorkoutStatus.UNKNOWN__
                        result.add(
                            Exercise(
                                it.id,
                                it.title,
                                formatDateTime(it.dueDate as String?),
                                UserEventFilterType.WORKOUT,
                                teacherName,
                                courseTitle,
                                status,
                                null
                            )
                        )
                    }
                }

                workouts.postValue(result)

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}