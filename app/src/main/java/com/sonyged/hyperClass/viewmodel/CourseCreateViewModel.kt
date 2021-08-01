package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.google.gson.Gson
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.UpdateCourseMutation
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.CourseDetail
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Tag
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption
import com.sonyged.hyperClass.utils.diffDate
import com.sonyged.hyperClass.utils.formatServerTime
import com.sonyged.hyperClass.views.getCourseCoverImageList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CourseCreateViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        var isRunning = false
    }

    val courseDetail = MutableLiveData<CourseDetail>()

    val tags = arrayListOf<Tag>()
    val filterTags = MutableLiveData<List<Tag>>()

    private val itemSelected = hashMapOf<String, Boolean>()

    var imageSelected = -1
    var date = DATE_INVALID

    fun setCourseDetail(data: CourseDetail) {
        courseDetail.postValue(data)
        data.tags.forEach {
            itemSelected[it.id] = true
        }
    }

    fun setTags(data: List<Tag>) {
        tags.addAll(data)
    }

    fun removeTag(tag: Tag) {
        itemSelected.remove(tag.id)
    }

    fun addTags(newTag: HashMap<String, Boolean>): List<Tag> {
        itemSelected.putAll(newTag)
        val result = arrayListOf<Tag>()
        val map = hashMapOf<String, Boolean>()
        courseDetail.value?.tags?.forEach {
            if (itemSelected.containsKey(it.id)) {
                result.add(it)
                map[it.id] = true
            }
        }
        tags.forEach {
            if (itemSelected.containsKey(it.id) && !map.containsKey(it.id)) {
                result.add(it)
            }
        }
        return result
    }

    fun getTagsDialog(): List<Tag> {
        val result = arrayListOf<Tag>()
        tags.forEach {
            if (!itemSelected.containsKey(it.id)) {
                result.add(it)
            }
        }
        return result
    }

    fun geCoverImageList(): List<DefaultCourseCoverImageOption> {
        return getCourseCoverImageList()
    }

    fun filterTag(text: String) {
        if (isRunning) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            isRunning = true
            val result = arrayListOf<Tag>()
            val query = text.lowercase()
            tags.forEach {
                if (it.name.lowercase().contains(query)) {
                    result.add(it)
                }
            }
            filterTags.postValue(result)
            isRunning = false
        }
    }

    fun changeCourse(name: String, isAuto: Boolean) {
        if (status.value?.id == STATUS_LOADING) {
            return
        }
        status.value = Status(STATUS_LOADING)
        viewModelScope.launch(Dispatchers.Default) {
            val context = getApplication<Application>()
            val oldData = courseDetail.value
            if (oldData == null) {
                status.postValue(Status(STATUS_FAILED))
                return@launch
            }
            val nameChange = name != oldData.name
            val tagChange = if (oldData.tags.size == itemSelected.size) {
                var count = 0
                oldData.tags.forEach {
                    if (itemSelected.containsKey(it.id)) {
                        count++
                    }
                }
                count != itemSelected.size
            } else {
                true
            }
            val dateChange = date != DATE_INVALID && date != oldData.endDate
            val autoChange = isAuto != oldData.autoCreateLessonWorkout
            val images = geCoverImageList()
            val iconChange = if (imageSelected != -1) {
                images[imageSelected] != oldData.icon
            } else {
                true
            }
            Timber.d("changeCourse - nameChange: $nameChange - tagChange: $tagChange - dateChange: $dateChange - autoChange: $autoChange - iconChange: $iconChange")
            if (!nameChange && !tagChange && !dateChange && !autoChange && !iconChange) {
                status.postValue(Status(STATUS_SUCCESSFUL))
                return@launch
            }

            val serverDate = formatServerTime(if (date == DATE_INVALID) oldData.endDate else date)

            if (date != DATE_INVALID) {
                val diff = diffDate(System.currentTimeMillis(), date)
                Timber.d("changeCourse - diff: $diff")
                val value = Status(STATUS_FAILED)
                when {
                    diff < 0 -> {
                        value.extras.putString(KEY_ERROR_MSG, context.getString(R.string.put_end_date_after_today))
                        status.postValue(value)
                        return@launch
                    }
                    diff < 1 -> {
                        val error = context.getString(R.string.course_error_date, serverDate, "1")
                        value.extras.putString(KEY_ERROR_MSG, error)
                        status.postValue(value)
                        return@launch
                    }
                    diff > 365 -> {
                        val error = context.getString(R.string.course_error_date, serverDate, "365")
                        value.extras.putString(KEY_ERROR_MSG, error)
                        status.postValue(value)
                        return@launch
                    }
                }
            }

            val ids = arrayListOf<String>()
            val deletedIds = arrayListOf<String>()
            val map = hashMapOf<String, Boolean>()
            oldData.tags.forEach {
                if (!itemSelected.containsKey(it.id)) {
                    deletedIds.add(it.id)
                }
                map[it.id] = true
            }
            tags.forEach {
                if (itemSelected.containsKey(it.id) && !map.containsKey(it.id)) {
                    ids.add(it.id)
                }
            }

            val query = UpdateCourseMutation(
                id = oldData.id,
                name = Input.optional(name),
                expiredAt = Input.optional(serverDate),
                autoCreateLessonWorkout = Input.optional(isAuto),
                defaultImage = Input.optional(images[imageSelected]),
                schoolTagIds = Input.optional(ids),
                deletedSchoolTagIds = Input.optional(deletedIds)
            )

            Timber.d("changeCourse - ${oldData.id} - $name - $serverDate - $isAuto - ${images[imageSelected]} - $ids - $deletedIds")

            val response = ApiUtils.getApolloClient().mutate(query).await()
            Timber.d("changeCourse - response: $response")


            if (response.data?.courseUpdate?.asCourseMutationFailure?.errors.isNullOrEmpty()) {
                status.postValue(Status(STATUS_SUCCESSFUL))
                return@launch
            }

            val value = Status(STATUS_FAILED)
            val error = StringBuilder()
            run loop@{
                response.data?.courseUpdate?.asCourseMutationFailure?.errors?.forEach {
                    when (it.message) {
                        "139" -> {
                            error.append(context.getString(R.string.could_not_update_due_to_ongoing_lesson))
                        }
                        "153" -> {
                            error.append(context.getString(R.string.could_not_update_due_to_completed_course))
                        }
                        else -> {
                            error.append(Gson().toJson(it))
                            error.append("\n")
                        }
                    }
                }
            }
            value.extras.putString(KEY_ERROR_MSG, error.toString())
            status.postValue(value)
        }
    }
}