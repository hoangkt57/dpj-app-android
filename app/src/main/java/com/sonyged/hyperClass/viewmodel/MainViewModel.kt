package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.google.gson.Gson
import com.sonyged.hyperClass.PageLayoutQuery
import com.sonyged.hyperClass.TabCoursesQuery
import com.sonyged.hyperClass.TabHomeQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.model.User
import com.sonyged.hyperClass.type.LessonStatus
import com.sonyged.hyperClass.type.UserEventFilter
import com.sonyged.hyperClass.type.UserEventFilterType
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDate
import com.sonyged.hyperClass.utils.formatDateTime
import com.sonyged.hyperClass.views.getCourseCoverImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class MainViewModel(application: Application) : BaseViewModel(application) {

    val user = MutableLiveData<User>()

    val courses = MutableLiveData<List<Course>>()

    val type = MutableLiveData(UserEventFilterType.ALL)

    val dateRange = MutableLiveData<Pair<Long, Long>>()

    val exercises = dateRange.switchMap { dateRange ->
        type.switchMap { type ->
            liveData(Dispatchers.Default) {
                val from = formatDate(dateRange.first)
                val until = formatDate(dateRange.second)
                val result = loadHomeData(from, until, type)
                val temp = MutableLiveData<List<Exercise>>(result)
                emitSource(temp)
            }
        }
    }

    init {
        loadUserData()
        loadCourseData()
    }

    private suspend fun loadHomeData(from: String, until: String, type: UserEventFilterType): ArrayList<Exercise> {
        Timber.d("loadHomeData - from: $from - until: $until - type: $type - thread: ${Thread.currentThread()}")
        val result = arrayListOf<Exercise>()
        try {
            val isTeacher = user.value?.isTeacher ?: return result
            val homeQuery = TabHomeQuery(
                Input.optional(""),
                UserEventFilter(Input.optional(from), Input.optional(until), type),
                isTeacher
            )
            val pageResponse = ApiUtils.getApolloClient().query(homeQuery).await()

            pageResponse.data?.currentUser?.eventsConnection?.edges?.forEach { edge ->
                edge?.node?.asLesson?.fragments?.tabHomeLessonFragment?.let {
                    val teacherName = it.teacher.name ?: ""
                    val courseName = it.course.name ?: ""

                    val status = LessonStatus.UNKNOWN__

                    result.add(
                        Exercise(
                            it.id,
                            it.name,
                            formatDateTime(it.beginAt.toString()),
                            UserEventFilterType.LESSON,
                            teacherName,
                            courseName,
                            status,
                            it.kickUrl
                        )
                    )

                }
                edge?.node?.asWorkout?.fragments?.tabHomeWorkoutFragment?.let {
                    val teacherName = it.course.teacher.name ?: ""
                    val courseName = it.course.name ?: ""

                    val status = it.studentWorkout?.status ?: WorkoutStatus.UNKNOWN__

                    result.add(
                        Exercise(
                            it.id,
                            it.title,
                            formatDateTime(it.dueDate.toString()),
                            UserEventFilterType.WORKOUT,
                            teacherName,
                            courseName,
                            status,
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return result
    }

    private fun loadUserData() {
        Timber.d("loadUserData")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val userResponse = ApiUtils.getApolloClient().query(PageLayoutQuery()).await()
                Timber.d("loadUserData - user : ${userResponse.data}")

                userResponse.data?.currentUser?.let {
                    val data = User(
                        it.id,
                        it.loginId,
                        it.name ?: "",
                        it.password ?: "",
                        it.email ?: "",
                        it.__typename == "Teacher"
                    )
                    user.postValue(data)
                    sharedPref.setTeacher(data.isTeacher)
                }

                initDate()



            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun loadCourseData() {
        Timber.d("loadCourseData")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val pageResponse = ApiUtils.getApolloClient().query(TabCoursesQuery()).await()

                Timber.d("loadCourseData - data : ${Gson().toJson(pageResponse.data)}")

                val result = arrayListOf<Course>()

                pageResponse.data?.currentUser?.asTeacher?.assignedCourses?.forEach {
                    val node = it.fragments.tabCoursesFragment
                    val coverImage = getCourseCoverImage(node.coverImage.asDefaultCourseCoverImage?.value)
                    result.add(Course(node.id, node.name ?: "", coverImage, node.teacher.name ?: "", node.students.size))
                }

                pageResponse.data?.currentUser?.asStudent?.learningCourses?.forEach {
                    val node = it.fragments.tabCoursesFragment
                    val coverImage = getCourseCoverImage(node.coverImage.asDefaultCourseCoverImage?.value)
                    result.add(Course(node.id, node.name ?: "", coverImage, node.teacher.name ?: "", node.students.size))
                }

                courses.postValue(result)

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun initDate() {
        val calendar = Calendar.getInstance()
        val time1 = calendar.time.time
        calendar.add(Calendar.DATE, 7)
        val time2 = calendar.time.time
        dateRange.postValue(Pair(time1, time2))
    }

    fun logout() {
        sharedPref.setToken("")
    }


}