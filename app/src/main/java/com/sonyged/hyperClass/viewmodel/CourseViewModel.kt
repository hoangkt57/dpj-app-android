package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.SegLessonsQuery
import com.sonyged.hyperClass.SegWorkoutsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.TYPE_LESSON
import com.sonyged.hyperClass.constants.TYPE_WORKOUT
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.type.DateRange
import com.sonyged.hyperClass.type.LessonFilter
import com.sonyged.hyperClass.type.WorkoutFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

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

                Timber.d("loadData - data : ${lessonResponse}")

                val result = arrayListOf<Exercise>()

                lessonResponse.data?.node?.asCourse?.lessonsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val serverDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                        serverDf.timeZone = TimeZone.getTimeZone("UTC")
                        val date = serverDf.parse(it.beginAt.toString()) ?: ""
                        val clientDf = SimpleDateFormat("yy/MM/dd(E) HH:mm", Locale.JAPAN)

                        val teacherName = it.teacher.name ?: ""

                        result.add(Exercise(it.id, it.name, clientDf.format(date), TYPE_LESSON, teacherName, course.title))
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

//                Timber.d("loadData - data : ${Gson().toJson(pageResponse.data)}")

                val result = arrayListOf<Exercise>()

                workoutResponse.data?.node?.asCourse?.workoutsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val serverDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                        serverDf.timeZone = TimeZone.getTimeZone("UTC")
                        val date = serverDf.parse(it.dueDate.toString()) ?: ""
                        val clientDf = SimpleDateFormat("yy/MM/dd(E) HH:mm", Locale.JAPAN)

                        val teacherName = it.course.teacher.name ?: ""

                        result.add(Exercise(it.id, it.title, clientDf.format(date), TYPE_WORKOUT, teacherName, course.title))
                    }
                }

                workouts.postValue(result)

            } catch (e: Exception) {
                Timber.e(e)
            }


        }


    }

}