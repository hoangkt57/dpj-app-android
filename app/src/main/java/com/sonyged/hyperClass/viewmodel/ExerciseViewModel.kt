package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageWorkoutQuery
import com.sonyged.hyperClass.SegExplanationQuery
import com.sonyged.hyperClass.SegStudentsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ExerciseViewModel(application: Application, val isLesson: Boolean, val id: String) : BaseViewModel(application) {

    val workout = MutableLiveData<Workout>()

    val lesson = MutableLiveData<Lesson>()

    val students = MutableLiveData<List<Student>>()

    val info = MutableLiveData<Triple<String, String, String>>()

    init {
        if (isLesson) {
            loadStudent()
            loadLesson()
        } else {
            loadWorkout()
        }
    }


    private fun loadLesson() {
        Timber.d("loadLesson")
        viewModelScope.launch(Dispatchers.Default) {
            try {

                val lessonResponse = ApiUtils.getApolloClient().query(SegExplanationQuery(id)).await()

                val id = lessonResponse.data?.node?.asLesson?.id ?: ""
                val name = lessonResponse.data?.node?.asLesson?.name ?: ""
                val courseName = lessonResponse.data?.node?.asLesson?.course?.name ?: ""
                val teacher = lessonResponse.data?.node?.asLesson?.teacher?.name ?: ""
                val date = formatDateTime(lessonResponse.data?.node?.asLesson?.beginAt.toString())
                val studentCount = lessonResponse.data?.node?.asLesson?.students?.size ?: 0
                val kickUrl = lessonResponse.data?.node?.asLesson?.kickUrl

                lesson.postValue(Lesson(id, name, courseName, date, teacher, studentCount, kickUrl))

                info.postValue(Triple(name, date, teacher))

            } catch (e: Exception) {
                Timber.e(e)
            }
        }

    }

    private fun loadStudent() {
        Timber.d("loadStudent")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = arrayListOf<Student>()
                val studentResponse = ApiUtils.getApolloClient().query(SegStudentsQuery(id)).await()

                studentResponse.data?.node?.asLesson?.studentsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        result.add(Student(it.id, it.name ?: "", 0, it.__typename))
                    }
                }

                students.postValue(result)

            } catch (e: Exception) {
                Timber.e(e)
            }
        }

    }

    private fun loadWorkout() {
        Timber.d("loadWorkout")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = arrayListOf<Student>()
                val workoutQuery = PageWorkoutQuery(id, sharedPref.isTeacher(), "")
                val workoutResponse = ApiUtils.getApolloClient().query(workoutQuery).await()

                val id = workoutResponse.data?.node?.asWorkout?.id ?: ""
                val name = workoutResponse.data?.node?.asWorkout?.title ?: ""
                val courseName = workoutResponse.data?.node?.asWorkout?.course?.name ?: ""
                val description = workoutResponse.data?.node?.asWorkout?.description ?: ""
                val date = formatDateTime(workoutResponse.data?.node?.asWorkout?.dueDate.toString())
                val data = ""
                val status = workoutResponse.data?.node?.asWorkout?.studentWorkout?.status ?: WorkoutStatus.UNKNOWN__
                val yourAnswer = ""
                val file = ""

                workoutResponse.data?.node?.asWorkout?.studentsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        result.add(Student(it.id, it.name ?: "", 0, it.__typename))
                    }
                }

                students.postValue(result)

                workout.postValue(Workout(id, name, courseName, description, date, data, status, yourAnswer, file))

                info.postValue(Triple(name, date, ""))

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}