package com.sonyged.hyperClass.model

import com.apollographql.apollo.api.EnumValue

enum class TeacherStatus(
    override val rawValue: String
) : EnumValue {
    VERIFY("VERIFY");
}
