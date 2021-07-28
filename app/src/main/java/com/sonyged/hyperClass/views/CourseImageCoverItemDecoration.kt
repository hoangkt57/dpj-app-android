package com.sonyged.hyperClass.views

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R

class CourseImageCoverItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val horizontal = context.resources.getDimensionPixelSize(R.dimen.exercise_space_horizontal)
    private val vertical = context.resources.getDimensionPixelSize(R.dimen.exercise_space_vertical)


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.top = vertical
        outRect.left = horizontal / 3
        outRect.right = horizontal / 3
        outRect.bottom = vertical
    }
}