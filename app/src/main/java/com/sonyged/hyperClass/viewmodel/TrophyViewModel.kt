package com.sonyged.hyperClass.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.await
import com.sonyged.hyperClass.PageStudentTrophyQuery
import com.sonyged.hyperClass.api.ApiUtils
import com.sonyged.hyperClass.constants.DEFAULT_HAND_RAISED
import com.sonyged.hyperClass.constants.DEFAULT_SPEAK
import com.sonyged.hyperClass.constants.DEFAULT_TROPHY
import com.sonyged.hyperClass.model.StudentTrophy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class TrophyViewModel(application: Application, private val courseId: String) : BaseViewModel(application) {

    val studentTrophy = MutableLiveData<StudentTrophy>()

    init {
        loadStudentTrophy()
    }

    private fun loadStudentTrophy() {
        Timber.d("loadStudentTrophy")
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val query = PageStudentTrophyQuery(courseId)
                val response = ApiUtils.getApolloClient().query(query).await()
//                Timber.d("loadStudentTrophy - response: $response")
                val map1 = hashMapOf<String, Int>()
                val map2 = hashMapOf<String, Int>()
                response.data?.node?.asCourse?.achievements?.forEach { achievement ->
                    achievement.achievement.rules.forEach { rule ->
                        map1[achievement.achievement.name] = rule.activationValue ?: 0
                    }
                }
                response.data?.currentUser?.coursesConnection?.edges?.forEach { edge ->
                    map2[DEFAULT_TROPHY] = edge?.node?.stats?.trophyCount ?: 0
                    map2[DEFAULT_SPEAK] = edge?.node?.stats?.speakDurationInSec ?: 0
                    map2[DEFAULT_HAND_RAISED] = edge?.node?.stats?.raisedHandCount ?: 0
                }
                val data = StudentTrophy(
                    response.data?.currentUser?.id ?: "",
                    response.data?.currentUser?.name ?: "",
                    response.data?.node?.asCourse?.name ?: "",
                    map1[DEFAULT_TROPHY] ?: 0,
                    map1[DEFAULT_SPEAK] ?: 0,
                    map1[DEFAULT_HAND_RAISED] ?: 0,
                    map2[DEFAULT_TROPHY] ?: 0,
                    map2[DEFAULT_SPEAK] ?: 0,
                    map2[DEFAULT_HAND_RAISED] ?: 0
                )
                studentTrophy.postValue(data)
            } catch (e: Exception) {
                Timber.e(e, "loadStudentTrophy")
            }
        }
    }
}