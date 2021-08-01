package com.sonyged.hyperClass.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.*
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.model.Person
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.type.*
import com.sonyged.hyperClass.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class LessonCreateViewModel(application: Application, val courseId: String) :
    BaseViewModel(application) {

    val lesson = MutableLiveData<Lesson>()
    val teacherList = arrayListOf<Student>()
    val teachers = MutableLiveData<List<Student>>()

    var name1 = ""
    var name2 = ""

    var teacher: Person? = null
    var assistant: Person? = null
    var date: Long = DATE_INVALID
    var startTime: Pair<Int, Int>? = null
    var endTime: Pair<Int, Int>? = null

    var frequencyType: Int = 0
    var endDate: Long = DATE_INVALID
    val days = arrayListOf<DayOfWeek>()

    init {
        loadTeacher()
    }

    fun isEditing(): Boolean {
        return courseId.isEmpty()
    }

    fun setName(name: String) {
        val index = name.lastIndexOf(" ")
        name1 = name
        name2 = name.substring(0, index)
    }

    private fun loadTeacher() {
        Timber.d("loadTeacher")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = arrayListOf<Student>()
                val teacherResponse = ApiUtils.getApolloClient().query(
                    ModalSettingLessonQuery(
                        first = Input.optional(100),
                        filter = Input.optional(
                            UserFilter(role = Input.optional(UserRoleFilter.TEACHER))
                        )
                    )
                ).await()

//                Timber.d("loadTeacher - teacherResponse: $teacherResponse")

                teacherResponse.data?.currentUser?.school?.membersConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        result.add(Student(it.id, it.name ?: "", 0, it.__typename))
                    }

                }
                teacherList.clear()
                teacherList.addAll(result)
                teachers.postValue(result)

            } catch (e: Exception) {
                Timber.e(e, "loadTeacher")
            }
        }
    }

    fun filterTeacher(text: String) {
        if (status.value?.id == STATUS_FILTERING) {
            return
        }
        status.value = Status(STATUS_FILTERING)
        viewModelScope.launch(Dispatchers.Default) {
            val result = arrayListOf<Student>()
            val query = text.lowercase()
            teacherList.forEach {
                if (it.name.lowercase().contains(query)) {
                    result.add(it)
                }
            }
            teachers.postValue(result)
            status.postValue(Status(STATUS_NONE))
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun createLesson(
        name: String,
        isRecord: Boolean,
        isCutCameraAreaInRecordedVideo: Boolean,
        isRepeatEnable: Boolean,
        isUntil: Boolean,
        isRepeatTime: Boolean,
        frequencyText: String,
        repeatTimeText: String,
        isEditRepeatOnce: Boolean
    ) {
        Timber.d("createLesson")
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            val context = getApplication<Application>()
            try {
                Timber.d(
                    "createLesson - name: $name - teacher: $teacher" +
                            "- isRecord: $isRecord - isCutCameraAreaInRecordedVideo: $isCutCameraAreaInRecordedVideo" +
                            " - isRepeatEnable: $isRepeatEnable - frequencyText: $frequencyText - frequencyType: $frequencyType - days: $days" +
                            " - repeatTimeText: $repeatTimeText - isUntil: $isUntil - isRepeatTime: $isRepeatTime"
                )

                if (name.isEmpty()) {
                    sendErrorStatus(context.getString(R.string.title_required))
                    return@launch
                }
                if (teacher == null) {
                    sendErrorStatus(context.getString(R.string.teacher_required))
                    return@launch
                }

                if (startTime == null || endTime == null || date == DATE_INVALID) {
                    sendErrorStatus(context.getString(R.string.start_time_after_5_from_current))
                    return@launch
                }

                val time0 = getCurrentTimeUTC()
                val time1 = localTimeToUTC(date, startTime!!.first, startTime!!.second)
                val time2 = localTimeToUTC(date, endTime!!.first, endTime!!.second)
                val diff = ((time2 - time1) / 1000).toInt()

                val startTimeServer = formatServerTime(time1)
                val endTimeServer = formatServerTime(time2)

                Timber.d(
                    "createLesson - time0: $time0 - time1: $time1 - time2: $time2 - diff: $diff - date: $date " +
                            "- startTime: $startTime - endTime: $endTime" +
                            "- startTimeServer: $startTimeServer - endTimeServer: $endTimeServer"
                )

                if (diff < 15 * 60 || (isRepeatEnable && isUntil && endDate != DATE_INVALID && endDate < date)) {
                    sendErrorStatus(context.getString(R.string.class_time_15_or_more))
                    return@launch
                }
                if (time1 - time0 < 5 * 60) {
                    sendErrorStatus(context.getString(R.string.start_time_after_5_from_current))
                    return@launch
                }

                val assistant = if (assistant != null) {
                    Input.optional(assistant!!.id)
                } else {
                    Input.absent()
                }

                if (isEditing()) {
                    val lesson = lesson.value
                    if (lesson == null || lesson.id.isEmpty()) {
                        status.postValue(Status(STATUS_FAILED))
                        return@launch
                    }

                    if (isEditRepeatOnce && lesson.batchId != null) {
                        val beginAt = onlyTimeFromTime(lesson.beginAt)
                        val endAt = onlyTimeFromTime(lesson.endAt)
                        val delta1 = timeInSecond(startTime!!) - timeInSecond(beginAt)
                        val delta2 = timeInSecond(endTime!!) - timeInSecond(endAt)

                        Timber.d("createLesson - isEditing - batchId - delta1: $delta1 - delta2: $delta2")
                        val query = BatchUpdateCourseLessonMutation(
                            lesson.batchId,
                            Input.optional(name),
                            Input.absent(),
                            Input.optional(isRecord),
                            Input.optional(isRecord && isCutCameraAreaInRecordedVideo),
                            Input.absent(),
                            Input.optional(teacher!!.id),
                            assistant,
                            Input.absent(),
                            Input.optional(delta1),
                            Input.optional(delta2)
                        )

                        val response = ApiUtils.getApolloClient().mutate(query).await()
                        Timber.d("createLesson - isEditing - batchId - response: $response")

                        if (response.data?.lessonBatchUpdate?.asLessonMutationFailure?.errors.isNullOrEmpty()) {
                            status.postValue(Status(STATUS_SUCCESSFUL))
                            return@launch
                        }
                        sendErrorStatus(response.data?.lessonBatchUpdate?.asLessonMutationFailure?.errors)
                        return@launch
                    }

                    val query = UpdateCourseLessonMutation(
                        lesson.id,
                        Input.optional(name),
                        Input.optional(startTimeServer),
                        Input.optional(endTimeServer),
                        Input.optional(isRecord),
                        Input.optional(isRecord && isCutCameraAreaInRecordedVideo),
                        Input.optional(teacher!!.id),
                        assistant
                    )
                    val response = ApiUtils.getApolloClient().mutate(query).await()
                    Timber.d("createLesson - isEditing - response: $response")

                    if (response.data?.lessonUpdate?.asLessonMutationFailure?.errors.isNullOrEmpty()) {
                        status.postValue(Status(STATUS_SUCCESSFUL))
                        return@launch
                    }
                    sendErrorStatus(response.data?.lessonUpdate?.asLessonMutationFailure?.errors)
                    return@launch
                }

                if (!isRepeatEnable) {
                    val query = CreateCourseLessonMutation(
                        courseId,
                        name,
                        startTimeServer,
                        endTimeServer,
                        isRecord,
                        isRecord && isCutCameraAreaInRecordedVideo,
                        teacher!!.id,
                        assistant
                    )
                    val response = ApiUtils.getApolloClient().mutate(query).await()
                    Timber.d("createLesson - response: $response")

                    if (response.data?.lessonCreate?.asLessonMutationFailure?.errors.isNullOrEmpty()) {
                        status.postValue(Status(STATUS_SUCCESSFUL))
                        return@launch
                    }

                    sendErrorStatus(response.data?.lessonCreate?.asLessonMutationFailure?.errors)
                    return@launch
                }

                if (isUntil && endDate == DATE_INVALID) {
                    sendErrorStatus(context.getString(R.string.condition_required))
                    return@launch
                }
                if (frequencyText.isEmpty()) {
                    sendErrorStatus(context.getString(R.string.frequency_required))
                    return@launch
                }
                if (isRepeatTime && repeatTimeText.isEmpty()) {
                    sendErrorStatus(context.getString(R.string.condition_required))
                    return@launch
                }
                if (frequencyType == 1 && days.isEmpty()) {
                    sendErrorStatus(context.getString(R.string.day_of_week_required))
                    return@launch
                }
                val frequency = frequencyText.toInt()
                val repeatTime = if (isRepeatTime) repeatTimeText.toInt() else -1

                val daily = if (frequencyType == 0) {
                    Input.optional(DailyInput(frequency))
                } else {
                    Input.absent()
                }
                val weekly = if (frequencyType == 1) {
                    Input.optional(WeeklyInput(frequency, days))
                } else {
                    Input.absent()
                }
                val endAfterNthInstance = if (isRepeatTime && repeatTime != -1) {
                    Input.optional(repeatTime)
                } else {
                    Input.absent()
                }
                val endDateServer = formatServerTime(endDate) as Any
                val endAfterDate = if (isUntil && endDate != DATE_INVALID) {
                    Input.optional(endDateServer)
                } else {
                    Input.absent()
                }

                Timber.d("createLesson - endDateServer: $endDateServer")

                val query = BatchCreateCourseLessonMutation(
                    courseId,
                    name,
                    startTimeServer,
                    diff,
                    isRecord,
                    Input.optional(isRecord && isCutCameraAreaInRecordedVideo),
                    teacher!!.id,
                    assistant,
                    Input.absent(),
                    RecurrenceInput(
                        daily,
                        weekly,
                        RecurrenceEndInput(
                            endAfterNthInstance,
                            endAfterDate
                        )
                    )
                )

                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("createLesson - isRepeatEnable - response: $response")

                if (response.data?.lessonBatchCreate?.asLessonMutationFailure?.errors.isNullOrEmpty()) {
                    status.postValue(Status(STATUS_SUCCESSFUL))
                    return@launch
                }

                sendErrorStatus(response.data?.lessonBatchCreate?.asLessonMutationFailure?.errors)
            } catch (e: Exception) {
                Timber.e(e, "createLesson")
                sendErrorStatus("")
            }
        }
    }


}