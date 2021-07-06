package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.SegLessonsQuery
import com.sonyged.hyperClass.SegWorkoutsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.type.*
import com.sonyged.hyperClass.utils.formatDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CourseViewModel(application: Application, val course: Course) : BaseViewModel(application) {

    val lessons = MutableLiveData<List<Exercise>>()

    val workouts = MutableLiveData<List<Exercise>>()

    init {
        loadLessons()
        loadWorkouts()
    }

    private fun loadLessons() {
        Timber.d("loadLessons")

        viewModelScope.launch(Dispatchers.Default) {
            try {

                val lessonsQuery = SegLessonsQuery(
                    course.id,
                    filter = Input.optional(
                        LessonFilter(
                            beginAtBetween = Input.optional(
                                DateRange(
                                    Input.optional("2021-06-30"),
                                    Input.optional("2021-07-09")
                                )
                            )
                        )
                    )
                )

                val lessonResponse = ApiUtils.getApolloClient().query(lessonsQuery).await()

//                Timber.d("loadData - data : ${lessonResponse}")

                val result = arrayListOf<Exercise>()

                lessonResponse.data?.node?.asCourse?.lessonsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val teacherName = it.teacher.name ?: ""

                        result.add(
                            Exercise(
                                it.id,
                                it.name,
                                formatDateTime(it.beginAt.toString()),
                                UserEventFilterType.LESSON,
                                teacherName,
                                course.title,
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

                val workoutsQuery = SegWorkoutsQuery(
                    course.id,
                    isTeacher = true,
                    filter = Input.optional(
                        WorkoutFilter(
                            dueDateBetween = Input.optional(
                                DateRange(
                                    Input.optional("2021-06-30"),
                                    Input.optional("2021-07-09")
                                )
                            )
                        )
                    )
                )

                val workoutResponse = ApiUtils.getApolloClient().query(workoutsQuery).await()

                val result = arrayListOf<Exercise>()

                workoutResponse.data?.node?.asCourse?.workoutsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val teacherName = it.course.teacher.name ?: ""
                        val status = it.studentWorkout?.status ?: WorkoutStatus.UNKNOWN__
                        result.add(
                            Exercise(
                                it.id,
                                it.title,
                                formatDateTime(it.dueDate.toString()),
                                UserEventFilterType.WORKOUT,
                                teacherName,
                                course.title,
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