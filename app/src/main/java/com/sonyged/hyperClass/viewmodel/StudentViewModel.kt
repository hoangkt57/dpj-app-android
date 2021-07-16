package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageStudentQuery
import com.sonyged.hyperClass.SegStudentLessonsQuery
import com.sonyged.hyperClass.SegStudentWorkoutsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.model.StudentPage
import com.sonyged.hyperClass.type.LessonStatus
import com.sonyged.hyperClass.type.UserEventFilterType
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class StudentViewModel(application: Application, val studentId: String) : BaseViewModel(application) {

    val student = MutableLiveData<StudentPage>()

    val lessons = MutableLiveData<List<Exercise>>()

    val workouts = MutableLiveData<List<Exercise>>()

    init {
        loadStudent()
        loadLessons()
        loadWorkouts()
    }

    private fun loadStudent() {
        Timber.d("loadStudent")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val studentResponse = ApiUtils.getApolloClient().query(PageStudentQuery(studentId)).await()

                studentResponse.data?.node?.asStudent?.let {
                    val studentPage = StudentPage(
                        it.id,
                        it.loginId,
                        it.name ?: "",
                        it.password ?: "",
                        it.email ?: "",
                    )
                    it.learningCourses.forEach { course ->
                        studentPage.courses.add(course.name ?: "")
                    }

                    it.schoolTags?.forEach { tag ->
                        studentPage.tag.add(tag.name)
                    }

                    student.postValue(studentPage)
                }

            } catch (e: Exception) {
                Timber.e(e, "loadStudent")
            }
        }
    }

    private fun loadLessons() {
        Timber.d("loadLessons")

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = arrayListOf<Exercise>()
                val lessonResponse = ApiUtils.getApolloClient().query(SegStudentLessonsQuery(studentId)).await()

                lessonResponse.data?.node?.asStudent?.lessonsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val teacherName = it.teacher.name ?: ""
                        val courseName = it.course.name ?: ""

                        result.add(
                            Exercise(
                                it.id,
                                it.name,
                                formatDateTime(it.beginAt as String?),
                                UserEventFilterType.LESSON,
                                teacherName,
                                courseName,
                                LessonStatus.UNKNOWN__,
                                null
                            )
                        )
                    }
                }

                lessons.postValue(result)

            } catch (e: Exception) {
                Timber.e(e, "loadLessons")
            }
        }
    }

    private fun loadWorkouts() {
        Timber.d("loadWorkouts")

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = arrayListOf<Exercise>()
                val workoutResponse = ApiUtils.getApolloClient().query(SegStudentWorkoutsQuery(studentId)).await()

                workoutResponse.data?.node?.asStudent?.workoutsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {

                        val teacherName = it.course.teacher.name ?: ""
                        val courseName = it.course.name ?: ""

                        result.add(
                            Exercise(
                                it.id,
                                it.title,
                                formatDateTime(it.dueDate as String?),
                                UserEventFilterType.WORKOUT,
                                teacherName,
                                courseName,
                                WorkoutStatus.UNKNOWN__,
                                null
                            )
                        )
                    }
                }

                workouts.postValue(result)

            } catch (e: Exception) {
                Timber.e(e, "loadWorkouts")
            }
        }
    }

}