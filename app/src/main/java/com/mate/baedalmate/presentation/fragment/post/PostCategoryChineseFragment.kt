package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentPostCategoryChineseBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryChineseFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryChineseBinding>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()
    private lateinit var postCategoryListAdapter: PostCategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCategoryChineseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecruitList()
        initListAdapter()
        setCategoryClickListener()
    }

    private fun getRecruitList(sort: String = "deadlineDate") {
        recruitViewModel.requestCategoryRecruitList(
            categoryId = 2,
            page = 0,
            size = 25,
            sort = sort
        )
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter()
        binding.rvPostCategoryChineseList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryChineseList.adapter = postCategoryListAdapter
        }
        recruitViewModel.recruitListChinese.observe(viewLifecycleOwner) { recruitList ->
            postCategoryListAdapter.submitList(recruitList.recruitList.toMutableList())
        }

        postCategoryListAdapter.setOnItemClickListener(object :
            PostCategoryListAdapter.OnItemClickListener {
            override fun postClick(postId: Int, pos: Int) {
                findNavController().navigate(
                    PostCategoryListFragmentDirections.actionPostCategoryListFragmentToPostFragment(
                        postId = postId
                    )
                )
            }
        })
    }

    private fun setCategoryClickListener() {
        // TODO 백엔드 수정시 수정 필요
        binding.radiogroupLayoutPostCategoryChineseSort.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_post_category_chinese_sort_time -> { getRecruitList(sort = "") }
                R.id.radiobutton_post_category_chinese_sort_star -> { getRecruitList(sort = "") }
                R.id.radiobutton_post_category_chinese_sort_popular -> { getRecruitList(sort = "") }
            }
        }
    }
}