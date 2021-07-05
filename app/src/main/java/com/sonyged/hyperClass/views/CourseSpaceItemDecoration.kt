package com.sonyged.hyperClass.views

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R

class CourseSpaceItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val horizontal = context.resources.getDimensionPixelSize(R.dimen.exercise_space_horizontal)
    private val vertical = context.resources.getDimensionPixelSize(R.dimen.exercise_space_vertical)


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if (position == 0) {
            outRect.top = vertical * 2
        } else {
            outRect.top = vertical
        }

        outRect.left = horizontal
        outRect.right = horizontal
        outRect.bottom = vertical
    }
}