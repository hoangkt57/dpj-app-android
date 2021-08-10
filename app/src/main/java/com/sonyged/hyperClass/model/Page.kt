package com.sonyged.hyperClass.model

import com.sonyged.hyperClass.constants.DEFAULT_PAGE_ID

data class Page(
    override val id: String,
    val start: String?,
    val after: String?
) : BaseItem() {

    companion object {
        fun empty(): Page {
            return Page(DEFAULT_PAGE_ID, null, null)
        }
    }
}
