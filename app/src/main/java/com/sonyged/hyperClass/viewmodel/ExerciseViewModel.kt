package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.*
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.api.ApiUtils.Companion.BASE_URL_UPLOAD
import com.sonyged.hyperClass.constants.DATE_INVALID
import com.sonyged.hyperClass.constants.STATUS_LOADING
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
        Timber.d("loadWorkout - id: $id")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val context = getApplication<Application>()
                val result = arrayListOf<Student>()
                val query = PageWorkoutQuery(id, sharedPref.isTeacher(), BASE_URL_UPLOAD)
                val response = ApiUtils.getApolloClient().query(query).await()
                val id = response.data?.node?.asWorkout?.id ?: ""
                val studentWorkoutId = response.data?.node?.asWorkout?.studentWorkout?.id ?: ""
                val name = response.data?.node?.asWorkout?.title ?: ""
                val courseName = response.data?.node?.asWorkout?.course?.name ?: ""
                val description = response.data?.node?.asWorkout?.description ?: ""
                val date = formatDateTimeToLong(response.data?.node?.asWorkout?.dueDate as String?)
                val submissionAt = formatDateTimeToLong(response.data?.node?.asWorkout?.studentWorkout?.submittedAt as String?)
                val status = response.data?.node?.asWorkout?.studentWorkout?.status
                val answer = response.data?.node?.asWorkout?.studentWorkout?.description ?: ""
                val files = arrayListOf<Attachment>()
                response.data?.node?.asWorkout?.attachments?.forEach { attachment ->
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
                val submissionFile = arrayListOf<Attachment>()
                response.data?.node?.asWorkout?.studentWorkout?.attachments?.forEach { attachment ->
                    submissionFile.add(
                        Attachment(
                            attachment.id,
                            formatDateTimeToLong(attachment.createdAt as String?),
                            attachment.filename,
                            attachment.contentType,
                            attachment.url
                        )
                    )
                }
                val comments = response.data?.node?.asWorkout?.studentWorkout?.comments
                val comment = if (!comments.isNullOrEmpty()) {
                    comments[comments.size - 1].content
                } else {
                    ""
                }
                response.data?.node?.asWorkout?.studentsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        result.add(
                            Student(
                                it.id,
                                it.name ?: "",
                                0,
                                it.__typename,
                                edge.studentWorkout?.status,
                                StatusResource.getStudentStatus(context, edge.studentWorkout?.status),
                                edge.studentWorkout?.id
                            )
                        )
                    }
                }
                students.postValue(result)
                workout.postValue(
                    Workout(
                        id,
                        studentWorkoutId,
                        name,
                        description,
                        date,
                        status,
                        files,
                        submissionAt,
                        answer,
                        submissionFile,
                        comment
                    )
                )
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
                sendSuccessStatus()
            } catch (e: Exception) {
                Timber.e(e, "deleteLesson")
                sendErrorStatus()
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
                sendSuccessStatus()
            } catch (e: Exception) {
                Timber.e(e, "deleteWorkout")
                sendErrorStatus()
            }
        }
    }

    fun verifyWorkout(studentWorkoutId: String) {
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        Timber.d("verifyWorkout")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = MarkStudentWorkoutCompleteMutation(studentWorkoutId)
                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("verifyWorkout - response: $response")
                sendSuccessStatus()
            } catch (e: Exception) {
                Timber.e(e, "verifyWorkout")
                sendErrorStatus()
            }
        }
    }

}