package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentPostCategoryAllBinding
import com.mate.baedalmate.databinding.ItemEmptyPostCategoryViewBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryAllFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryAllBinding>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()
    private lateinit var postCategoryListAdapter: PostCategoryListAdapter
    private lateinit var glideRequestManager: RequestManager
    private val constraintSet = ConstraintSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCategoryAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecruitList()
        initListAdapter()
        setCategoryListContents()
        setCategoryClickListener()
    }

    private fun getRecruitList(sort: String = "deadlineDate") {
        recruitViewModel.requestCategoryRecruitList(
            page = 0,
            size = 10000,
            sort = sort
        )
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter(requestManager = glideRequestManager)
        binding.rvPostCategoryAllList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryAllList.adapter = postCategoryListAdapter
        }
    }

    private fun setCategoryClickListener() {
        binding.radiogroupLayoutPostCategoryAllSort.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_post_category_all_sort_time -> {
                    getRecruitList(sort = "deadlineDate")
                }
                R.id.radiobutton_post_category_all_sort_star -> {
                    getRecruitList(sort = "score")
                }
                R.id.radiobutton_post_category_all_sort_popular -> {
                    getRecruitList(sort = "view")
                }
            }
        }
    }

    private fun setCategoryListContents() {
        val emptyPostCategoryViewBinding =
            ItemEmptyPostCategoryViewBinding.inflate(LayoutInflater.from(binding.root.context))
        emptyPostCategoryViewBinding.tvEmptyPostCategoryGuideNotAppear.text = "현재 모집글이 없어요"
        val emptyView = emptyPostCategoryViewBinding.root
        addEmptyView(emptyView)

        recruitViewModel.recruitListAll.observe(viewLifecycleOwner) { recruitList ->
            if (recruitList.recruitList.isNotEmpty()) {
                postCategoryListAdapter.submitList(recruitList.recruitList.toMutableList())
                constraintSet.clone(binding.layoutPostCategoryListAll)
                constraintSet.setVisibility(emptyView.id, View.GONE)
                constraintSet.applyTo(binding.layoutPostCategoryListAll)
            }
            else {
                constraintSet.clone(binding.layoutPostCategoryListAll)
                constraintSet.setVisibility(emptyView.id, View.VISIBLE)
                constraintSet.applyTo(binding.layoutPostCategoryListAll)
            }
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

    private fun addEmptyView(emptyView: View) {
        binding.layoutPostCategoryListAll.addView(emptyView)

        constraintSet.clone(binding.layoutPostCategoryListAll)
        constraintSet.connect(emptyView.id, ConstraintSet.TOP, binding.layoutPostCategoryListAll.id, ConstraintSet.TOP, 0)
        constraintSet.connect(emptyView.id, ConstraintSet.BOTTOM, binding.layoutPostCategoryListAll.id, ConstraintSet.BOTTOM, 0)
        constraintSet.connect(emptyView.id, ConstraintSet.START, binding.layoutPostCategoryListAll.id, ConstraintSet.START, 0)
        constraintSet.connect(emptyView.id, ConstraintSet.END, binding.layoutPostCategoryListAll.id, ConstraintSet.END, 0)
        constraintSet.applyTo(binding.layoutPostCategoryListAll)
    }
}