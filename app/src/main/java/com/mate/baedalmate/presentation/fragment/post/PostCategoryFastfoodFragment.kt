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
import com.mate.baedalmate.databinding.FragmentPostCategoryFastfoodBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryFastfoodFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryFastfoodBinding>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()
    private lateinit var postCategoryListAdapter: PostCategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCategoryFastfoodBinding.inflate(inflater, container, false)
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
            categoryId = 5,
            page = 0,
            size = 25,
            sort = sort
        )
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter()
        binding.rvPostCategoryFastfoodList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryFastfoodList.adapter = postCategoryListAdapter
        }
        recruitViewModel.recruitListFastfood.observe(viewLifecycleOwner) { recruitList ->
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
        binding.radiogroupLayoutPostCategoryFastfoodSort.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_post_category_fastfood_sort_time -> { getRecruitList(sort = "") }
                R.id.radiobutton_post_category_fastfood_sort_star -> { getRecruitList(sort = "") }
                R.id.radiobutton_post_category_fastfood_sort_popular -> { getRecruitList(sort = "") }
            }
        }
    }
}