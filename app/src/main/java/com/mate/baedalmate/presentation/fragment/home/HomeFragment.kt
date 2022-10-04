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
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ViewPagerUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitDto
import com.mate.baedalmate.databinding.FragmentHomeBinding
import com.mate.baedalmate.presentation.adapter.HomeCategoryAdapter
import com.mate.baedalmate.presentation.adapter.HomeRecentPostAdapter
import com.mate.baedalmate.presentation.adapter.HomeRecommendPostAdapter
import com.mate.baedalmate.presentation.adapter.HomeTopPostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeBinding>()
    private lateinit var homeTopPostAdapter: HomeTopPostAdapter
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter
    private lateinit var homeRecentPostAdapter: HomeRecentPostAdapter
    private lateinit var homeRecommendPostAdapter: HomeRecommendPostAdapter
    private lateinit var homeTopPostindicators: Array<ImageView?>

    private var testArrTopPost1 = RecruitDto(
        createDate = "2022-07-17T09:34:29.220Z",
        currentPeople = 1,
        deadlineDate = "2022-07-27T09:34:29.220Z",
        deliveryFee = 4000,
        dormitory = "수림학사",
        id = 1,
        minPeople = 2,
        minPrice = 14000,
        restaurantName = "교촌치킨",
        thumbnailImage = "",
        title = "같이 치킨 먹을 사람!",
        userScore = 4.1f,
        username = "유상"
    )
    private var testArrTopPost2 = RecruitDto(
        createDate = "2022-07-12T09:34:29.220Z",
        currentPeople = 1,
        deadlineDate = "2022-07-21T09:34:29.220Z",
        deliveryFee = 2000,
        dormitory = "수림학사",
        id = 2,
        minPeople = 2,
        minPrice = 14000,
        restaurantName = "삼겹살",
        thumbnailImage = "",
        title = "사람 구해요",
        userScore = 2.1f,
        username = "유상"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        initTopUserUI()
        initTopPostUI()
        initCategoryUI()
        initRecentPostUI()
        initRecommendPostListUI()
        initNavigation()
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
        // TODO 서버 연결
        homeTopPostAdapter.submitList(
            mutableListOf(
                testArrTopPost1,
                testArrTopPost2,
                testArrTopPost1,
                testArrTopPost2
            )
        )
        setupIndicators(homeTopPostAdapter.itemCount)
        setViewPagerChangeEvent()
    }

    private fun setupIndicators(count: Int) {
        homeTopPostindicators = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in homeTopPostindicators.indices) {
            homeTopPostindicators[i] = ImageView(requireContext())
            homeTopPostindicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_vp_indicator_inactive)
            )
            homeTopPostindicators[i]!!.layoutParams = params
            binding.vpHomeTopPostsIndicators.addView(homeTopPostindicators[i])
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
        // TODO 서버 연결
        homeCategoryAdapter.submitList(MutableList(8, { i -> "i" }))
    }

    private fun initRecentPostUI() {
        homeRecentPostAdapter = HomeRecentPostAdapter()
        binding.rvHomeContentsBottomPostRecentList.adapter = homeRecentPostAdapter
        // TODO 서버 연결
        homeRecentPostAdapter.submitList(
            mutableListOf(
                testArrTopPost1,
                testArrTopPost2,
                testArrTopPost1,
                testArrTopPost2
            )
        )
    }

    private fun initRecommendPostListUI() {
        homeRecommendPostAdapter = HomeRecommendPostAdapter()
        binding.rvHomeContentsBottomPostRecommendList.adapter = homeRecommendPostAdapter
        // TODO 서버 연결
        homeRecommendPostAdapter.submitList(
            mutableListOf(
                testArrTopPost1,
                testArrTopPost2,
                testArrTopPost1,
                testArrTopPost2
            )
        )
    }

    private fun initNavigation() {
        binding.fabHomeToWrite.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postCategoryListFragment)
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