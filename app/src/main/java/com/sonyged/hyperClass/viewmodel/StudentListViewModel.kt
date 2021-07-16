package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageCourseStudentsQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class StudentListViewModel(application: Application, val course: Course) : BaseViewModel(application) {

    val students = MutableLiveData<List<Student>>()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        Timber.d("loadStudents")

        viewModelScope.launch(Dispatchers.Default) {
            try {

                val result = arrayListOf<Student>()

                val studentQuery = PageCourseStudentsQuery(course.id)
                val studentResponse = ApiUtils.getApolloClient().query(studentQuery).await()
                Timber.d("loadStudents - studentResponse: $studentResponse")
                studentResponse.data?.node?.asCourse?.studentsConnection?.edges?.forEach { edge ->
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
}