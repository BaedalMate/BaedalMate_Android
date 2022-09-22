package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentPostCategoryListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryListFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryListBinding>()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCategoryListBinding.inflate(inflater, container, false)
        initViewPager()
        return binding.root
    }

    private fun initTabLayout() {
        val tabLayout = binding.tablayoutPostCategoryListCategory
        for (i in 0 until tabLayout.tabCount) {
            val tab: TabLayout.Tab? = tabLayout.getTabAt(i)
            if (tab != null) {
                val tabTextView = TextView(requireContext())
                tab.customView = tabTextView
                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.text = tab.text
                tabTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_000000
                    )
                )

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTextAppearance(R.style.style_title2_kor)
                }
            }
        }
    }

    private fun setTabLayout() {
        binding.tablayoutPostCategoryListCategory.addOnTabSelectedListener(object :
            OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.vpPostCategoryListContents.currentItem = tab.position
                val text = tab.customView as TextView?
                text!!.setTextAppearance(R.style.style_title2_kor)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val text = tab.customView as TextView?
                text!!.setTextAppearance(R.style.style_body1_kor)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initViewPager() {
        viewPager = binding.vpPostCategoryListContents
        tabLayout = binding.tablayoutPostCategoryListCategory
        val pagerAdapter = PostCategoryListFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        pagerAdapter.addFragment(PostCategoryAllFragment())
        viewPager.adapter = pagerAdapter

        tabLayout.addTab(tabLayout.newTab().setText("전체"))
        tabLayout.addTab(tabLayout.newTab().setText("1인분"))
        tabLayout.addTab(tabLayout.newTab().setText("치킨"))
        tabLayout.addTab(tabLayout.newTab().setText("한식"))
        tabLayout.addTab(tabLayout.newTab().setText("중식"))
        tabLayout.addTab(tabLayout.newTab().setText("일식"))
        tabLayout.addTab(tabLayout.newTab().setText("양식"))
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
                pagerAdapter.refreshFragment(position, pagerAdapter.fragments[position])
            }
        })

        for (i in 0 until pagerAdapter.itemCount) {
            pagerAdapter.refreshFragment(i, pagerAdapter.fragments[i])
        }

        initTabLayout()
        setTabLayout()
    }
}