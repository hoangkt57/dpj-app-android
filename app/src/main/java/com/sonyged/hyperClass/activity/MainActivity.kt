package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.MainPageAdapter
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityMainBinding
import com.sonyged.hyperClass.fragment.OnTitleClickListener
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.observer.Observer
import com.sonyged.hyperClass.viewmodel.MainViewModel
import timber.log.Timber

class MainActivity : BaseActivity(), Observer {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter: MainPageAdapter by lazy {
        MainPageAdapter(this)
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        AppObserver.getInstance().addObserver(this)

    }

    override fun onDestroy() {
        super.onDestroy()

        AppObserver.getInstance().removeAllObserver()
    }

    private fun setupView() {

        binding.viewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 2
            adapter = this@MainActivity.adapter
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_home -> {
                    binding.viewPager.setCurrentItem(0, true)
                    changeTitle(viewModel.rangeDateText, true)
                }
                R.id.page_course -> {
                    binding.viewPager.setCurrentItem(1, true)
                    changeTitle(getString(R.string.course), false)
                }
                R.id.page_setting -> {
                    binding.viewPager.setCurrentItem(2, true)
                    changeTitle(getString(R.string.setting), false)
                }
            }
            true
        }

        binding.titleLayout.setOnClickListener {
            Timber.d("title layout - position: ${binding.viewPager.currentItem}")
            val fragment = supportFragmentManager.findFragmentByTag("f" + binding.viewPager.currentItem)
            if (fragment is OnTitleClickListener) {
                fragment.onTitleClick()
            }
        }

    }

    fun setTitleDateRange(title: String) {
        viewModel.rangeDateText = title
        changeTitle(title, true)
    }

    private fun changeTitle(title: String, clickable: Boolean) {
        binding.title.text = title
        binding.titleLayout.isClickable = clickable
        binding.arrow.visibility = if (clickable) View.VISIBLE else View.INVISIBLE
    }

    override fun onEvent(event: Int, data: Bundle?) {
        Timber.d("onEvent - event: $event")
        when (event) {
            EVENT_LESSON_CHANGE, EVENT_WORKOUT_CHANGE -> {
                viewModel.loadExercises()
            }
            EVENT_COURSE_CHANGE, EVENT_STUDENT_CHANGE, EVENT_COURSE_DETAIL_CHANGE -> {
                viewModel.loadCourseData()
            }
        }
    }


}