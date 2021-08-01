package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.*
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.STATUS_LOADING
import com.sonyged.hyperClass.constants.STATUS_SUCCESSFUL
import com.sonyged.hyperClass.model.*
import com.sonyged.hyperClass.type.LessonBatchDeleteInput
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDateTime
import com.sonyged.hyperClass.utils.formatDateTimeToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ExerciseViewModel(application: Application, val isLesson: Boolean, val id: String) :
    BaseViewModel(application) {

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


    fun loadLesson() {
        Timber.d("loadLesson")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val response = ApiUtils.getApolloClient().query(SegExplanationQuery(id)).await()
                response.data?.node?.asLesson?.let {
                    val beginAt = formatDateTimeToLong(it.beginAt as String?)
                    val endAt = formatDateTimeToLong(it.endAt as String?)
                    val data = Lesson(
                        it.id,
                        it.batchId,
                        it.name,
                        it.course.name ?: "",
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
                        beginAt,
                        endAt,
                        it.students.size,
                        it.kickUrl
                    )
                    lesson.postValue(data)
                    val date = formatDateTime(it.beginAt as String?)
                    info.postValue(Triple(it.name, date, it.teacher.name ?: ""))
                }
            } catch (e: Exception) {
                Timber.e(e, "loadLesson")
            }
        }

    }

    private fun loadStudent() {
        Timber.d("loadStudent")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = arrayListOf<Student>()
                val studentResponse = ApiUtils.getApolloClient().query(SegStudentsQuery(id)).await()
//                Timber.d("loadStudent - studentResponse: $studentResponse")
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

    fun loadWorkout() {
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
                val date = formatDateTimeToLong(workoutResponse.data?.node?.asWorkout?.dueDate as String?)
                val status = workoutResponse.data?.node?.asWorkout?.studentWorkout?.status
                    ?: WorkoutStatus.UNKNOWN__

                val files = arrayListOf<Attachment>()
                workoutResponse.data?.node?.asWorkout?.attachments?.forEach { attachment ->
                    files.add(
                        Attachment(
                            attachment.id,
                            attachment.filename,
                            attachment.contentType,
                            attachment.url
                        )
                    )
                }
                workoutResponse.data?.node?.asWorkout?.studentsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        result.add(Student(it.id, it.name ?: "", 0, it.__typename))
                    }
                }

                students.postValue(result)
                workout.postValue(Workout(id, name, courseName, description, date, status, files))
                info.postValue(Triple(name, "", courseName))
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun deleteLesson(id: String?, isBatch: Boolean) {
        if (status.value?.id == STATUS_LOADING || id.isNullOrEmpty()) {
            return
        }
        status.value = Status(STATUS_LOADING)
        Timber.d("deleteLesson")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = if (isBatch) {
                    BatchDeleteCourseLessonMutation(LessonBatchDeleteInput(id))
                } else {
                    DeleteCourseLessonMutation(arrayListOf(id))
                }
                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("deleteLesson - response: $response")
                status.postValue(Status(STATUS_SUCCESSFUL))
            } catch (e: Exception) {
                Timber.e(e, "deleteLesson")
            }
        }
    }

    fun deleteWorkout(id: String?) {
        if (status.value?.id == STATUS_LOADING || id.isNullOrEmpty()) {
            return
        }
        status.value = Status(STATUS_LOADING)
        Timber.d("deleteWorkout")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = WorkoutDeleteMutation(id)
                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("deleteWorkout - response: $response")
                status.postValue(Status(STATUS_SUCCESSFUL))
            } catch (e: Exception) {
                Timber.e(e, "deleteWorkout")
            }
        }
    }

}