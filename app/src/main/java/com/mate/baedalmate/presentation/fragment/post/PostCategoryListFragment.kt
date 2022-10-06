package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args by navArgs<PostCategoryListFragmentArgs>()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initViewpagerCurrentCategory()
        setWriteClickListener()
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
        pagerAdapter.addFragment(PostCategoryKoreanFragment())
        pagerAdapter.addFragment(PostCategoryChineseFragment())
        pagerAdapter.addFragment(PostCategoryJapaneseFragment())
        pagerAdapter.addFragment(PostCategoryWesternFragment())
        pagerAdapter.addFragment(PostCategoryFastfoodFragment())
        pagerAdapter.addFragment(PostCategoryBunsikFragment())
        pagerAdapter.addFragment(PostCategoryDessertFragment())
        pagerAdapter.addFragment(PostCategoryChickenFragment())
        pagerAdapter.addFragment(PostCategoryPizzaFragment())
        pagerAdapter.addFragment(PostCategoryAsiaFragment())
        pagerAdapter.addFragment(PostCategoryPackedmealFragment())
        viewPager.adapter = pagerAdapter

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_all)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_korean)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_chinese)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_japanese)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_western)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_fastfood)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_bunsik)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_dessert)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_chicken)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_pizza)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_asia)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.category_packedmeal)))
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

    private fun initViewpagerCurrentCategory() {
        when (args.currentCategory) {
            getString(R.string.category_all) -> { setCurrentSelectedTabLayout(0) }
            getString(R.string.category_korean) -> { setCurrentSelectedTabLayout(1) }
            getString(R.string.category_chinese) -> { setCurrentSelectedTabLayout(2) }
            getString(R.string.category_japanese) -> { setCurrentSelectedTabLayout(3) }
            getString(R.string.category_western) -> { setCurrentSelectedTabLayout(4) }
            getString(R.string.category_fastfood) -> { setCurrentSelectedTabLayout(5) }
            getString(R.string.category_bunsik) -> { setCurrentSelectedTabLayout(6) }
            getString(R.string.category_dessert) -> { setCurrentSelectedTabLayout(7) }
            getString(R.string.category_chicken) -> { setCurrentSelectedTabLayout(8) }
            getString(R.string.category_pizza) -> { setCurrentSelectedTabLayout(9) }
            getString(R.string.category_asia) -> { setCurrentSelectedTabLayout(10) }
            getString(R.string.category_packedmeal) -> { setCurrentSelectedTabLayout(11) }
        }
    }

    private fun setCurrentSelectedTabLayout(index: Int) {
        handler.postDelayed(Runnable { tabLayout.getTabAt(index)?.select() }, 100)
        viewPager.setCurrentItem(index, false)
    }

    private fun setWriteClickListener() {
        binding.fabPostCategoryToWrite.setOnClickListener {
            findNavController().navigate(R.id.action_postCategoryListFragment_to_writeCategoryFragment)
        }
    }
}