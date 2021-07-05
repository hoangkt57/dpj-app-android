package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.MainPageAdapter
import com.sonyged.hyperClass.databinding.ActivityMainBinding
import com.sonyged.hyperClass.viewmodel.MainViewModel

class MainActivity : BaseActivity() {

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

    }

    private fun setupView() {

        binding.viewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 2
            adapter = this@MainActivity.adapter
        }

        changeTitle("2020年 11月", true)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_home -> {
                    binding.viewPager.setCurrentItem(0, true)
                    changeTitle("2020年 11月", true)
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
    }

    private fun changeTitle(title: String, clickable: Boolean) {
        binding.title.text = title
        binding.titleLayout.isClickable = clickable
        binding.arrow.visibility = if (clickable) View.VISIBLE else View.INVISIBLE
    }


}