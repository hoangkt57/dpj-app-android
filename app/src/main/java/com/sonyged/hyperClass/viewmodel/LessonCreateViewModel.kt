package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.ModalSettingLessonQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.type.UserFilter
import com.sonyged.hyperClass.type.UserRoleFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LessonCreateViewModel(application: Application, val data: Lesson) :
    BaseViewModel(application) {

    val lesson = MutableLiveData<Lesson>()
    val teacherList = arrayListOf<Student>()
    val teachers = MutableLiveData<List<Student>>()

    var teacher : Student? = null

    companion object {
        var isRunning = false
    }

    init {
        if (isEditing()) {
            lesson.postValue(data)
        }
        loadTeacher()
    }

    fun isEditing(): Boolean {
        return data.id.isNotEmpty()
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

                Timber.d("loadTeacher - teacherResponse: $teacherResponse")

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
        if (isRunning) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            isRunning = true
            val result = arrayListOf<Student>()
            teacherList.forEach {
                if (it.name.contains(text)) {
                    result.add(it)
                }
            }
            teachers.postValue(result)
            isRunning = false
        }
    }


}