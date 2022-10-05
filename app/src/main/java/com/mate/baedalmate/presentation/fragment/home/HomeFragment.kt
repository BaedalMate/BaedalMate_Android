package com.mate.baedalmate.presentation.fragment.home

import android.os.Bundle
import android.text.Annotation
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ViewPagerUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitDto
import com.mate.baedalmate.databinding.FragmentHomeBinding
import com.mate.baedalmate.domain.model.TagDto
import com.mate.baedalmate.presentation.adapter.HomeCategoryAdapter
import com.mate.baedalmate.presentation.adapter.HomeRecentPostAdapter
import com.mate.baedalmate.presentation.adapter.HomeRecommendPostAdapter
import com.mate.baedalmate.presentation.adapter.HomeTopPostAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeBinding>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()
    private lateinit var homeTopPostAdapter: HomeTopPostAdapter
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter
    private lateinit var homeRecentPostAdapter: HomeRecentPostAdapter
    private lateinit var homeRecommendPostAdapter: HomeRecommendPostAdapter
    private lateinit var homeTopPostIndicators: Array<ImageView?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        getRecruitListData()
        initUI()
        initNavigation()
        return binding.root
    }

    private fun getRecruitListData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recruitViewModel.requestHomeRecruitTagList(sort = "deadlineDate")
                recruitViewModel.requestHomeRecruitRecentList(sort = "deadlineDate")
                recruitViewModel.requestHomeRecruitRecommendList(sort = "deadlineDate")
            }
        }
    }

    private fun initUI() {
        initTopUserUI()
        initTopPostUI()
        initCategoryUI()
        initRecentPostUI()
        initRecommendPostListUI()
    }

    private fun initTopUserUI() {
        // TODO 서버 연결
        val span = SpannableString("캡스톤")
        setRoundTextView(span, "rounded", 0, span.length)
        binding.tvHomeTopTitleUserName.text = span
        binding.tvHomeTopTitleLocationCurrent.text = "서울과기대 누리학사"
    }

    private fun initTopPostUI() {
        homeTopPostAdapter = HomeTopPostAdapter()
        with(binding.vpHomeTopPosts) {
            adapter = homeTopPostAdapter
            ViewPagerUtil.previewNextItem(this, 37.dp, 15.dp)
        }

        recruitViewModel.recruitHomeTagList.observe(viewLifecycleOwner) { tagList ->
            val submitTagList = tagList.recruitList.toMutableList()
            submitTagList.add(
                TagRecruitDto(
                    "", "", "", 0, "", 0, "", 0, listOf(TagDto("")),
                    0f, ""
                )
            )
            homeTopPostAdapter.submitList(submitTagList)
            setupIndicators(homeTopPostAdapter.itemCount)
            setViewPagerChangeEvent()
        }
    }

    private fun setupIndicators(count: Int) {
        binding.vpHomeTopPostsIndicators.removeAllViews()
        homeTopPostIndicators = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in homeTopPostIndicators.indices) {
            homeTopPostIndicators[i] = ImageView(requireContext())
            homeTopPostIndicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_vp_indicator_inactive)
            )
            homeTopPostIndicators[i]!!.layoutParams = params
            binding.vpHomeTopPostsIndicators.addView(homeTopPostIndicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setViewPagerChangeEvent() {
        binding.vpHomeTopPosts.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount: Int = binding.vpHomeTopPostsIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.vpHomeTopPostsIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_vp_indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_vp_indicator_inactive)
                )
            }
        }
    }

    private fun initCategoryUI() {
        homeCategoryAdapter = HomeCategoryAdapter()
        binding.rvHomeContentsBottomMenu.adapter = homeCategoryAdapter
        homeCategoryAdapter.submitList(
            mutableListOf(
                getString(R.string.category_all), getString(R.string.category_korean),
                getString(R.string.category_chinese), getString(R.string.category_japanese),
                getString(R.string.category_western), getString(R.string.category_fastfood),
                getString(R.string.category_bunsik), getString(R.string.category_dessert),
                getString(R.string.category_chicken), getString(R.string.category_pizza),
                getString(R.string.category_asia), getString(R.string.category_packedmeal)
            )
        )
    }

    private fun initRecentPostUI() {
        homeRecentPostAdapter = HomeRecentPostAdapter()
        binding.rvHomeContentsBottomPostRecentList.adapter = homeRecentPostAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recruitViewModel.recruitHomeRecentList.observe(viewLifecycleOwner) { recruitList ->
                    homeRecentPostAdapter.submitList(recruitList.recruitList.toMutableList())
                }
            }
        }
    }

    private fun initRecommendPostListUI() {
        homeRecommendPostAdapter = HomeRecommendPostAdapter()
        binding.rvHomeContentsBottomPostRecommendList.adapter = homeRecommendPostAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recruitViewModel.recruitHomeRecommendList.observe(viewLifecycleOwner) { recruitList ->
                    homeRecommendPostAdapter.submitList(recruitList.recruitList.toMutableList())
                }
            }
        }

        binding.radiogroupHomeContentsBottomPostRecommendCategory.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                // TODO: 백엔드에서 정렬기준이 만들어지면 수정 필요
                R.id.radiobutton_home_contents_bottom_post_recommend_time -> {
                    recruitViewModel.requestHomeRecruitRecommendList(sort = "deadlineDate")
                }
                R.id.radiobutton_home_contents_bottom_post_recommend_popular -> {
                    recruitViewModel.requestHomeRecruitRecommendList(sort = "deadlineDate")
                }
                R.id.radiobutton_home_contents_bottom_post_recommend_star -> {
                    recruitViewModel.requestHomeRecruitRecommendList(sort = "deadlineDate")
                }
            }
        }
    }

    private fun initNavigation() {
        binding.fabHomeToWrite.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_writeCategoryFragment)
        }
    }

    private fun setRoundTextView(
        span: SpannableString,
        spanKey: String,
        startIdx: Int,
        textLength: Int
    ) {
        span.setSpan(
            Annotation("", spanKey),
            startIdx,
            startIdx + textLength,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C)),
            startIdx,
            startIdx + textLength,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}