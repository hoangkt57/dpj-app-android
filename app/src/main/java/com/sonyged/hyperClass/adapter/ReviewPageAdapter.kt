package com.sonyged.hyperClass.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sonyged.hyperClass.fragment.ReviewFragment
import com.sonyged.hyperClass.model.StudentWorkout

class ReviewPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val data = arrayListOf<StudentWorkout>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<StudentWorkout>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        if (position >= 0 && position < data.size) {
            return data[position].getIdToLong()
        }
        return RecyclerView.NO_ID
    }

    override fun containsItem(itemId: Long): Boolean {
        return data.any { it.getIdToLong() == itemId }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun createFragment(position: Int): Fragment {
        return ReviewFragment.create(data[position])

    }
}