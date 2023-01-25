package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.mate.baedalmate.presentation.adapter.post.PostCategoryListSortSpinnerAdapter
import com.mate.baedalmate.presentation.adapter.post.PostCategoryLoadStateAdapter
import com.mate.baedalmate.presentation.adapter.write.WriteSecondDormitorySpinnerAdapter
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
    private lateinit var spinnerAdapter: PostCategoryListSortSpinnerAdapter
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
        initSortSpinner()
        setDisplayAvailableRecruitPost()
        observeSortSpinnerSelectedItem()
        setCategoryListContents()
    }

    private fun getRecruitList(sort: String = "deadlineDate") {
        recruitViewModel.requestCategoryRecruitList(
            categoryId = null,
            exceptClose = binding.checkboxPostCategoryAllAvailable.isChecked,
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
            postCategoryListAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun initSortSpinner() {
        val items = resources.getStringArray(R.array.post_category_sort_list)
        spinnerAdapter = PostCategoryListSortSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner_post_category_sort_list,
            items.toMutableList()
        )
        binding.spinnerPostCategoryAllSort.adapter = spinnerAdapter
    }

    private fun observeSortSpinnerSelectedItem() {
        binding.spinnerPostCategoryAllSort.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    setSortSpinnerRequestQuery()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setSortSpinnerRequestQuery() {
        when (binding.spinnerPostCategoryAllSort.selectedItem) {
            getString(R.string.post_sort_createDate) -> getRecruitList(sort = "createDate")
            getString(R.string.post_sort_deadlineDate) -> getRecruitList(sort = "deadlineDate")
            getString(R.string.post_sort_score) -> getRecruitList(sort = "score")
            getString(R.string.post_sort_view) -> getRecruitList(sort = "view")
        }
    }

    private fun setDisplayAvailableRecruitPost() {
        binding.checkboxPostCategoryAllAvailable.setOnCheckedChangeListener { _, _ ->
            setSortSpinnerRequestQuery()
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
                                setScrollToTop()
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

    private fun setScrollToTop() {
        binding.rvPostCategoryAllList.scrollToPosition(0)
    }
}