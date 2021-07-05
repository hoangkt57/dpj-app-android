package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageLayoutQuery
import com.sonyged.hyperClass.TabHomeQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.TYPE_LESSON
import com.sonyged.hyperClass.constants.TYPE_WORKOUT
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.type.UserEventFilter
import com.sonyged.hyperClass.type.UserEventFilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : BaseViewModel(application) {

    val exercises = MutableLiveData<List<Exercise>>()

    init {
        loadData()
    }

    private fun loadData() {
        Timber.d("loadData")

        viewModelScope.launch(Dispatchers.Default) {
            try {

//                val userResponse = ApiUtils.getApolloClient().query(PageLayoutQuery()).await()
//                Timber.d("loadData - user : ${userResponse.data?.currentUser}")

                val homeQuery = TabHomeQuery(
                    Input.optional(""),
                    UserEventFilter(Input.optional("2021-06-30"), Input.optional("2021-07-07"), UserEventFilterType.ALL),
                    true
                )
                val pageResponse = ApiUtils.getApolloClient().query(homeQuery).await()

//                Timber.d("loadData - data : ${Gson().toJson(pageResponse.data)}")

                val result = arrayListOf<Exercise>()

                pageResponse.data?.currentUser?.eventsConnection?.edges?.forEach { edge ->
                    edge?.node?.asLesson?.fragments?.tabHomeLessonFragment?.let {
                        Timber.d("loadData - ${it.name}")

                        val serverDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                        serverDf.timeZone = TimeZone.getTimeZone("UTC")
                        val date = serverDf.parse(it.beginAt.toString()) ?: ""
                        val clientDf = SimpleDateFormat("yy/MM/dd(E) HH:mm", Locale.JAPAN)

                        val teacherName = it.teacher.name ?: ""
                        val courseName = it.course.name ?: ""

                        result.add(Exercise(it.id, it.name, clientDf.format(date), TYPE_LESSON, teacherName, courseName))

                    }
                    edge?.node?.asWorkout?.fragments?.tabHomeWorkoutFragment?.let {
                        Timber.d("loadData - ${it.title} - ${it.dueDate}")

                        val serverDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                        serverDf.timeZone = TimeZone.getTimeZone("UTC")
                        val date = serverDf.parse(it.dueDate.toString()) ?: ""
                        val clientDf = SimpleDateFormat("yy/MM/dd(E) HH:mm", Locale.JAPAN)

                        val teacherName = it.course.teacher.name ?: ""
                        val courseName = it.course.name ?: ""

                        result.add(Exercise(it.id, it.title, clientDf.format(date), TYPE_WORKOUT, teacherName, courseName))
                    }
                }

                exercises.postValue(result)

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}