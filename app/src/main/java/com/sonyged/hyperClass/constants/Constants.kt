package com.sonyged.hyperClass.constants

import com.sonyged.hyperClass.BuildConfig

const val LINK_TERM = "https://dist.hyperclass.jp/terms/HyperClass_TOU_v_1_0_0.html"
const val LINK_POLICY = "https://dist.hyperclass.jp/privacy/HyperClass_PP_v_1_0_0.html"
const val LINK_LICENSES = "https://dist.hyperclass.jp/misc/licenses/HyperClassLicense.html"

const val FILE_PROVIDER_AUTHORITY = "${BuildConfig.APPLICATION_ID}.fileprovider"

const val TEACHER = "Teacher"
const val STUDENT = "Student"

const val LOGIN_AGREEMENT_PP = 4
const val LOGIN_CHANGE_PASSWORD = 5
const val LOGIN_BOTH = 6

const val TYPE_EDIT_NAME = 1
const val TYPE_EDIT_EMAIL = 2

const val DATE_INVALID = -1L
const val DEFAULT_PAGE_ID = "-1"

const val DEFAULT_TROPHY = "DEFAULT_TROPHY_ACHIEVEMENT"
const val DEFAULT_SPEAK = "DEFAULT_SPEAK_ACHIEVEMENT"
const val DEFAULT_HAND_RAISED = "DEFAULT_HAND_RAISED_ACHIEVEMENT"

const val STATUS_NONE = -1
const val STATUS_LOADING = 1
const val STATUS_FAILED = 2
const val STATUS_SUCCESSFUL = 3
const val STATUS_FILTERING = 4
const val STATUS_ADD_SUCCESSFUL = 5
const val STATUS_DELETE_SUCCESSFUL = 6


const val BASE_EVENT = 1000
const val EVENT_LESSON_CHANGE = BASE_EVENT + 1
const val EVENT_WORKOUT_CHANGE = BASE_EVENT + 2
const val EVENT_STUDENT_CHANGE = BASE_EVENT + 3
const val EVENT_COURSE_CHANGE = BASE_EVENT + 4
const val EVENT_COURSE_DETAIL_CHANGE = BASE_EVENT + 5
const val EVENT_REVIEW_CHANGE = BASE_EVENT + 6



