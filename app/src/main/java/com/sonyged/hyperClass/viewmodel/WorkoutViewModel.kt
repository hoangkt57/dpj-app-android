package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageWorkoutQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WorkoutViewModel(application: Application, val workoutId: String) : BaseViewModel(application) {

    val workout = MutableLiveData<Workout>()

    init {
        loadWorkout()
    }

    private fun loadWorkout() {
        Timber.d("loadWorkout")
        viewModelScope.launch(Dispatchers.Default) {
            try {

                val workoutQuery = PageWorkoutQuery(workoutId, sharedPref.isTeacher(), "")
                val workoutResponse = ApiUtils.getApolloClient().query(workoutQuery).await()

                val id = workoutResponse.data?.node?.asWorkout?.id ?: ""
                val name = workoutResponse.data?.node?.asWorkout?.title ?: ""
                val courseName = workoutResponse.data?.node?.asWorkout?.course?.name ?: ""
                val description = workoutResponse.data?.node?.asWorkout?.description ?: ""
                val date = formatDateTime(workoutResponse.data?.node?.asWorkout?.dueDate.toString())
                val data = ""
                val status = workoutResponse.data?.node?.asWorkout?.studentWorkout?.status ?: WorkoutStatus.UNKNOWN__
                val yourAnswer = ""
                val file = ""

                workout.postValue(Workout(id, name, courseName, description, date, data, status, yourAnswer, file))

            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}