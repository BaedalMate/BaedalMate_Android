package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentPostCategoryAllBinding
import com.mate.baedalmate.databinding.ItemEmptyPostCategoryViewBinding
import com.mate.baedalmate.presentation.adapter.post.PostCategoryLoadStateAdapter
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
            categoryId = null,
            sort = sort
        )
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter(requestManager = glideRequestManager)
        binding.rvPostCategoryAllList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryAllList.adapter = postCategoryListAdapter.withLoadStateFooter(
                PostCategoryLoadStateAdapter { postCategoryListAdapter.retry() }
            )
            postCategoryListAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    recruitViewModel.recruitListAll.collectLatest { recruitList ->
                        postCategoryListAdapter.submitData(recruitList)
                    }
                }

                launch {
                    postCategoryListAdapter.loadStateFlow.map { it.refresh }
                        .distinctUntilChanged()
                        .collect {
                            if (it is LoadState.NotLoading) {
                                if (postCategoryListAdapter.itemCount == 0) {
                                    constraintSet.clone(binding.layoutPostCategoryListAll)
                                    constraintSet.setVisibility(emptyView.id, View.VISIBLE)
                                    constraintSet.applyTo(binding.layoutPostCategoryListAll)
                                } else {
                                    constraintSet.clone(binding.layoutPostCategoryListAll)
                                    constraintSet.setVisibility(emptyView.id, View.GONE)
                                    constraintSet.applyTo(binding.layoutPostCategoryListAll)
                                }
                            }
                        }
                }
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
        constraintSet.connect(
            emptyView.id,
            ConstraintSet.TOP,
            binding.layoutPostCategoryListAll.id,
            ConstraintSet.TOP,
            0
        )
        constraintSet.connect(
            emptyView.id,
            ConstraintSet.BOTTOM,
            binding.layoutPostCategoryListAll.id,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.connect(
            emptyView.id,
            ConstraintSet.START,
            binding.layoutPostCategoryListAll.id,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            emptyView.id,
            ConstraintSet.END,
            binding.layoutPostCategoryListAll.id,
            ConstraintSet.END,
            0
        )
        constraintSet.applyTo(binding.layoutPostCategoryListAll)
    }
}