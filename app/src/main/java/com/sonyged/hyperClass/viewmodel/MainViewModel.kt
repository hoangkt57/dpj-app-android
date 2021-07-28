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
import com.sonyged.hyperClass.UpdateInfoMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.model.Person
import com.sonyged.hyperClass.model.User
import com.sonyged.hyperClass.type.*
import com.sonyged.hyperClass.utils.formatDate
import com.sonyged.hyperClass.utils.formatDateTime
import com.sonyged.hyperClass.utils.range7DayFromCurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class MainViewModel(application: Application) : BaseViewModel(application) {

    val user = MutableLiveData<User>()

    val courses = MutableLiveData<List<Course>>()

    val type = MutableLiveData(UserEventFilterType.ALL)

    val dateRange = MutableLiveData<Pair<Long, Long>>()

    var rangeDateText = ""

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
        initDate()
        loadUserData()
        loadCourseData()
    }

    private suspend fun loadHomeData(
        from: String,
        until: String,
        type: UserEventFilterType
    ): ArrayList<Exercise> {
        Timber.d("loadHomeData - from: $from - until: $until - type: $type - thread: ${Thread.currentThread()}")
        val result = arrayListOf<Exercise>()
        try {
            val time = System.currentTimeMillis()
            val isTeacher = sharedPref.isTeacher()
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
                            formatDateTime(it.beginAt as String?),
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
                            formatDateTime(it.dueDate as String?),
                            UserEventFilterType.WORKOUT,
                            teacherName,
                            courseName,
                            status,
                            null
                        )
                    )
                }
            }

            Timber.d("loadHomeData - time: ${System.currentTimeMillis() - time}")

        } catch (e: Exception) {
            Timber.e(e)
        }
        return result
    }

    private fun loadUserData() {
        Timber.d("loadUserData")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val time = System.currentTimeMillis()
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
                    if (data.isTeacher != sharedPref.isTeacher()) {
                        sharedPref.setTeacher(data.isTeacher)
                        initDate()
                    }
                }

                Timber.d("loadUserData - time: ${System.currentTimeMillis() - time}")

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun loadCourseData() {
        Timber.d("loadCourseData")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val time = System.currentTimeMillis()
                val pageResponse = ApiUtils.getApolloClient().query(TabCoursesQuery()).await()

                Timber.d("loadCourseData - data : ${Gson().toJson(pageResponse.data)}")

                val result = arrayListOf<Course>()

                pageResponse.data?.currentUser?.asTeacher?.assignedCourses?.forEach {
                    val node = it.fragments.tabCoursesFragment
                    val tags = arrayListOf<String>()
                    it.schoolTagsConnection?.edges?.forEach { tagEdge ->
                        tagEdge?.node?.name?.let { tag ->
                            tags.add(tag)
                        }
                    }
                    result.add(
                        Course(
                            node.id,
                            node.name ?: "",
                            node.coverImage.asDefaultCourseCoverImage?.value ?: DefaultCourseCoverImageOption.UNKNOWN__,
                            Person(
                                node.teacher.id,
                                node.teacher.name ?: "",
                                node.teacher.__typename
                            ),
                            Person(
                                node.assistant?.id ?: "",
                                node.assistant?.name ?: "",
                                node.assistant?.__typename ?: ""
                            ),
                            node.students.size,
                            tags
                        )
                    )
                }

                pageResponse.data?.currentUser?.asStudent?.learningCourses?.forEach {
                    val node = it.fragments.tabCoursesFragment
                    val tags = arrayListOf<String>()
                    result.add(
                        Course(
                            node.id,
                            node.name ?: "",
                            node.coverImage.asDefaultCourseCoverImage?.value ?: DefaultCourseCoverImageOption.UNKNOWN__,
                            Person(
                                node.teacher.id,
                                node.teacher.name ?: "",
                                node.teacher.__typename
                            ),
                            Person(
                                node.assistant?.id ?: "",
                                node.assistant?.name ?: "",
                                node.assistant?.__typename ?: ""
                            ),
                            node.students.size,
                            tags
                        )
                    )
                }

                courses.postValue(result)
                Timber.d("loadCourseData - time: ${System.currentTimeMillis() - time}")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun initDate() {
        dateRange.postValue(range7DayFromCurrent())
    }

    fun logout() {
        sharedPref.setToken("")
        sharedPref.setUserId("")
        sharedPref.setTeacher(false)
        sharedPref.setLoginSuccess(false)
    }

    fun updateInfo(name: String, email: String) {
        Timber.d("updateInfo")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val id = user.value?.id ?: return@launch
                val userInput = UserUpdateInput(
                    id = id,
                    name = Input.optional(name),
                    email = Input.optional(email)
                )
                val infoResponse =
                    ApiUtils.getApolloClient().mutate(UpdateInfoMutation(userInput)).await()
                Timber.d("updateInfo - user : ${infoResponse.data}")

                infoResponse.data?.userUpdate?.asUserResult?.user?.let {
                    user.value?.copy(name = it.name ?: "", email = it.email ?: "")?.let {
                        user.postValue(it)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "updateInfo")
            }
        }
    }


}