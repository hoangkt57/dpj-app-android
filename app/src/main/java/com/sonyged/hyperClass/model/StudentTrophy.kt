package com.sonyged.hyperClass.model

data class StudentTrophy(
    val id: String,
    val name: String,
    val courseName: String,
    val trophyMaxCount: Int,
    val speakMaxDurationInSec: Int,
    val handMaxCount: Int,
    val trophyCount: Int,
    val speakDurationInSec: Int,
    val handCount: Int
) {
    fun getTrophyPercent(): Int {
        if (trophyMaxCount == 0) {
            return 0
        }
        return trophyCount * 100 / trophyMaxCount
    }

    fun getSpeakPercent(): Int {
        if (speakMaxDurationInSec == 0) {
            return 0
        }
        return speakDurationInSec * 100 / speakMaxDurationInSec
    }

    fun getHandPercent(): Int {
        if (handMaxCount == 0) {
            return 0
        }
        return handCount * 100 / handMaxCount
    }
}
