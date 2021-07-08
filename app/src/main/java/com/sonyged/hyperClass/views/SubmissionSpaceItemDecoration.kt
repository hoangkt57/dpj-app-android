package com.sonyged.hyperClass.views

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R

class SubmissionSpaceItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val horizontal = context.resources.getDimensionPixelSize(R.dimen.submission_space_horizontal)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

//        val position = parent.getChildAdapterPosition(view)

        outRect.left = horizontal
        outRect.right = horizontal
    }
}