package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageStudentWorkoutQuery
import com.sonyged.hyperClass.SendStudentWorkoutReviewMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.model.Attachment
import com.sonyged.hyperClass.model.StudentWorkout
import com.sonyged.hyperClass.type.WorkoutReviewStatus
import com.sonyged.hyperClass.utils.formatDateTimeToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewViewModel(application: Application, private val studentWorkoutId: String, private val ids: List<String>) :
    BaseViewModel(application) {

    val studentWorkouts = MutableLiveData<List<StudentWorkout>>()

    init {
        loadStudentWorkout()
    }

    private fun loadStudentWorkout() {
        viewModelScope.launch(Dispatchers.Default) {
            loadStudentWorkout(studentWorkoutId)?.let {
                studentWorkouts.postValue(arrayListOf(it))
            }
            val data = arrayListOf<StudentWorkout>()
            ids.forEach { id ->
                loadStudentWorkout(id)?.let {
                    data.add(it)
                }
            }
            studentWorkouts.postValue(data)
        }
    }

    private suspend fun loadStudentWorkout(id: String): StudentWorkout? {
        try {
            val query = PageStudentWorkoutQuery(id)
            val response = ApiUtils.getApolloClient().query(query).await()
            val data = response.data?.node?.asStudentWorkout ?: return null
            val comment = if (data.comments.isNotEmpty()) {
                data.comments[data.comments.size - 1].content
            } else {
                ""
            }
            val attachments = arrayListOf<Attachment>()
            data.attachments.forEach { attachment ->
                attachments.add(
                    Attachment(
                        attachment.id,
                        formatDateTimeToLong("" as String?),
                        attachment.filename,
                        attachment.contentType,
                        attachment.url
                    )
                )
            }
            return StudentWorkout(
                data.id,
                data.student.name ?: "",
                formatDateTimeToLong(data.submittedAt as String?),
                data.status.rawValue,
                null,
                data.description,
                comment,
                attachments
            )
        } catch (e: Exception) {
            Timber.e(e, "loadStudentWorkout - $id")
        }
        return null
    }

    fun sendReview(id: String, status: WorkoutReviewStatus, comment: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                Timber.d("sendReview - id: $id - status: $status - comment: $comment")
                val query = SendStudentWorkoutReviewMutation(id, status, comment)
                val response = ApiUtils.getApolloClient().mutate(query).await()
                Timber.d("sendReview - response: $response")

                val errors = response.data?.studentworkoutReview?.asStudentWorkoutMutationFailure?.errors
                if (errors.isNullOrEmpty()) {
                    sendSuccessStatus()
                    return@launch
                }
                sendErrorStatus(errors)
            } catch (e: Exception) {
                Timber.e(e, "sendReview - $id")
            }
        }
    }
}