package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ExerciseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.FragmentHomeBinding
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.viewmodel.HomeViewModel
import com.sonyged.hyperClass.views.ExerciseSpaceItemDecoration
import timber.log.Timber

class HomePageFragment : BaseFragment(R.layout.fragment_home), OnItemClickListener {

    companion object {

        fun create(): Fragment {
            return HomePageFragment()
        }
    }

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.bind(requireView())
    }

    private val adapter: ExerciseAdapter by lazy {
        ExerciseAdapter(this)
    }

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            addItemDecoration(ExerciseSpaceItemDecoration(requireContext()))
            adapter = this@HomePageFragment.adapter
        }

        viewModel.exercises.observe(viewLifecycleOwner) { updateExercises(it) }
    }

    private fun updateExercises(exercises: List<Exercise>) {
        Timber.d("updateExercises - size: ${exercises.size}")

        adapter.submitList(exercises)

    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")
    }

}