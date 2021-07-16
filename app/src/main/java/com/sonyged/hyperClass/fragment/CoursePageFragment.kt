package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.CourseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.FragmentCourseBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.utils.startCourseActivity
import com.sonyged.hyperClass.viewmodel.MainViewModel
import com.sonyged.hyperClass.views.CourseSpaceItemDecoration
import timber.log.Timber

class CoursePageFragment : BaseFragment(R.layout.fragment_course), OnItemClickListener {

    companion object {

        fun create(): Fragment {
            return CoursePageFragment()
        }
    }

    private val binding: FragmentCourseBinding by lazy {
        FragmentCourseBinding.bind(requireView())
    }

    private val adapter: CourseAdapter by lazy {
        CourseAdapter(this)
    }

    private val viewModel by viewModels<MainViewModel>(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loading.show()
        binding.recyclerView.apply {
            addItemDecoration(CourseSpaceItemDecoration(requireContext()))
            adapter = this@CoursePageFragment.adapter
        }

        viewModel.courses.observe(viewLifecycleOwner) { updateCourses(it) }

    }

    private fun updateCourses(courses: List<Course>) {
        Timber.d("updateCourses - size: ${courses.size}")

        adapter.submitList(courses)
        binding.loading.hide()
    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val course = adapter.getAdapterItem(position)
        startCourseActivity(requireContext(), course)
    }


}