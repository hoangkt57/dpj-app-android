package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.*
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.type.UserFilter
import com.sonyged.hyperClass.type.UserRoleFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class StudentListViewModel(application: Application, val course: Course) :
    BaseViewModel(application) {

    val students = MutableLiveData<List<Student>>()
    val allStudents = arrayListOf<Student>()
    val filterStudents = MutableLiveData<List<Student>>()

    val itemSelected = hashMapOf<String, Boolean>()

    companion object {
        var isRunning = false
    }

    init {
        loadStudents()
    }

    fun isOwner(): Boolean {
        return course.teacher.id == sharedPref.getUserId()
    }

    private fun loadStudents() {
        Timber.d("loadStudents")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result1 = arrayListOf<Student>()

                val studentQuery = PageCourseStudentsQuery(course.id)
                val studentResponse = ApiUtils.getApolloClient().query(studentQuery).await()
                Timber.d("loadStudents - studentResponse: $studentResponse")
                studentResponse.data?.node?.asCourse?.studentsConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        result1.add(Student(it.id, it.name ?: "", 0, it.__typename))
                    }
                }

                students.postValue(result1)

                val map = hashMapOf<String, Boolean>()
                result1.forEach {
                    map[it.id] = true
                }
                val result2 = arrayListOf<Student>()

                val allStudentQuery = ModalStudentsAddQuery(
                    first = Input.optional(200),
                    filter = Input.optional(
                        UserFilter(
                            aND = Input.optional(
                                arrayListOf(
                                    UserFilter(role = Input.optional(UserRoleFilter.STUDENT)),
                                    UserFilter(active = Input.optional(true))
                                )
                            )
                        )
                    )
                )
                val allStudentResponse = ApiUtils.getApolloClient().query(allStudentQuery).await()
                Timber.d("loadStudents - allStudentResponse: $allStudentResponse")
                allStudentResponse.data?.currentUser?.school?.membersConnection?.edges?.forEach { edge ->
                    edge?.node?.let {
                        if (map.contains(it.id)) {
                            return@forEach
                        }
                        result2.add(Student(it.id, it.name ?: "", 0, it.__typename))
                    }
                }

                allStudents.clear()
                allStudents.addAll(result2)
                filterStudents.postValue(result2)

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun filterStudent(text: String) {
        if (isRunning) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            isRunning = true
            val result = arrayListOf<Student>()
            allStudents.forEach {
                if (it.name.contains(text)) {
                    result.add(it)
                }
            }
            filterStudents.postValue(result)
            isRunning = false
        }
    }

    fun deleteStudent(studentId: String) {
        if (isRunning) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            isRunning = true

            val query = RemoveCourseStudentsMutation(course.id, arrayListOf(studentId))
            val deleteResponse = ApiUtils.getApolloClient().mutate(query).await()
            Timber.d("deleteStudent - deleteResponse: $deleteResponse")
            if (deleteResponse.errors.isNullOrEmpty()) {
                val result = arrayListOf<Student>()
                students.value?.forEach {
                    if (it.id != studentId) {
                        result.add(it)
                    }
                }
                students.postValue(result)
                launch(Dispatchers.Main) {
                    Toast.makeText(getApplication(), R.string.student_deleted, Toast.LENGTH_SHORT).show()
                }
            }
            isRunning = false
        }
    }

    fun addStudent() {
        if (isRunning || itemSelected.isEmpty() || allStudents.isEmpty()) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            isRunning = true

            val data = arrayListOf<Student>()
            allStudents.forEach {
                if (itemSelected.containsKey(it.id)) {
                    data.add(it)
                }
            }
            val ids = arrayListOf<String>()
            data.forEach {
                ids.add(it.id)
            }
            val query = AddCourseStudentsMutation(course.id, ids)
            val response = ApiUtils.getApolloClient().mutate(query).await()
            Timber.d("addStudent - response: $response")
            if (response.errors.isNullOrEmpty()) {
                val result = arrayListOf<Student>()
                students.value?.let {
                    result.addAll(it)
                }
                result.addAll(0, data)
                students.postValue(result)
                launch(Dispatchers.Main) {
                    Toast.makeText(getApplication(), R.string.added, Toast.LENGTH_SHORT).show()
                }
            }
            isRunning = false
        }
    }

}