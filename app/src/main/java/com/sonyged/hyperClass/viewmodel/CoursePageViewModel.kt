package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.google.gson.Gson
import com.sonyged.hyperClass.TabCoursesQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.views.getCourseCoverImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CoursePageViewModel(application: Application) : BaseViewModel(application) {

    val courses = MutableLiveData<List<Course>>()

    init {
        loadData()
    }

    private fun loadData() {
        Timber.d("loadData")

        viewModelScope.launch(Dispatchers.Default) {
            try {

                val pageResponse = ApiUtils.getApolloClient().query(TabCoursesQuery()).await()

                Timber.d("loadData - data : ${Gson().toJson(pageResponse.data)}")

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

}