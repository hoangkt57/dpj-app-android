package com.sonyged.hyperClass.constants

const val LINK_TERM = "https://dist.hyperclass.jp/terms/HyperClass_TOU_v_1_0_0.html"
const val LINK_POLICY = "https://dist.hyperclass.jp/privacy/HyperClass_PP_v_1_0_0.html"
const val LINK_LICENSES = "https://dist.hyperclass.jp/misc/licenses/HyperClassLicense.html"

const val TEACHER = "Teacher"
const val STUDENT = "Student"

const val LOGIN_CHECKING = 1
const val LOGIN_FAILED = 2
const val LOGIN_SUCCESSFUL = 3
const val LOGIN_AGREEMENT_PP = 4
const val LOGIN_CHANGE_PASSWORD = 5
const val LOGIN_BOTH = 6

const val TYPE_ALL = 1
const val TYPE_LESSON = 2
const val TYPE_WORKOUT = 3

const val TYPE_EDIT_NAME = 1
const val TYPE_EDIT_EMAIL = 2


const val PASSWORD_ERROR_NONE = 0
const val PASSWORD_ERROR_NOT_EQUAL_CONFIRM = 1
const val PASSWORD_ERROR_LENGTH_8 = 2
const val PASSWORD_ERROR_SAME_OLD_PASS = 3
const val PASSWORD_ERROR_NEED_LETTER = 4
const val PASSWORD_ERROR_NEED_NUMBER = 5
const val PASSWORD_ERROR_INVALID = 6

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



