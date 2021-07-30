package com.sonyged.hyperClass.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.*
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.type.UserFilter
import com.sonyged.hyperClass.type.UserRoleFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class StudentListViewModel(application: Application, val courseId: String, val teacherId: String) :
    BaseViewModel(application) {

    val students = MutableLiveData<List<Student>>()
    val allStudents = arrayListOf<Student>()
    val filterStudents = MutableLiveData<List<Student>>()

    val itemSelected = hashMapOf<String, Boolean>()

    init {
        loadStudents()
    }

    fun isOwner(): Boolean {
        return teacherId == sharedPref.getUserId()
    }

    private fun loadStudents() {
        Timber.d("loadStudents")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result1 = arrayListOf<Student>()

                val studentQuery = PageCourseStudentsQuery(courseId)
                val studentResponse = ApiUtils.getApolloClient().query(studentQuery).await()
//                Timber.d("loadStudents - studentResponse: $studentResponse")
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
//                Timber.d("loadStudents - allStudentResponse: $allStudentResponse")
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
        if (status.value?.id == STATUS_FILTERING) {
            return
        }
        status.value = Status(STATUS_FILTERING)
        viewModelScope.launch(Dispatchers.Default) {
            val result = arrayListOf<Student>()
            val query = text.lowercase()
            allStudents.forEach {
                if (it.name.lowercase().contains(query)) {
                    result.add(it)
                }
            }
            filterStudents.postValue(result)
        }
    }

    fun deleteStudent(studentId: String) {
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            val query = RemoveCourseStudentsMutation(courseId, arrayListOf(studentId))
            val deleteResponse = ApiUtils.getApolloClient().mutate(query).await()
//            Timber.d("deleteStudent - deleteResponse: $deleteResponse")
            if (deleteResponse.data?.courseRemoveStudent?.asCourseMutationFailure?.errors.isNullOrEmpty()) {
                val result = arrayListOf<Student>()
                students.value?.forEach {
                    if (it.id != studentId) {
                        result.add(it)
                    }
                }
                students.postValue(result)
                status.postValue(Status(STATUS_DELETE_SUCCESSFUL))
            } else {
                status.postValue(Status(STATUS_FAILED))
            }
        }
    }

    fun addStudent() {
        if (itemSelected.isEmpty() || allStudents.isEmpty() || status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
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
            val query = AddCourseStudentsMutation(courseId, ids)
            val response = ApiUtils.getApolloClient().mutate(query).await()
            Timber.d("addStudent - response: $response")
            if (response.data?.courseAddStudent?.asCourseMutationFailure?.errors.isNullOrEmpty()) {
                val result = arrayListOf<Student>()
                students.value?.let {
                    result.addAll(it)
                }
                result.addAll(0, data)
                students.postValue(result)
                status.postValue(Status(STATUS_ADD_SUCCESSFUL))
            } else {
                status.postValue(Status(STATUS_FAILED))
            }
        }
    }

}