package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentPostCategoryChickenBinding
import com.mate.baedalmate.databinding.ItemEmptyPostCategoryViewBinding
import com.mate.baedalmate.presentation.adapter.post.PostCategoryListSortSpinnerAdapter
import com.mate.baedalmate.presentation.adapter.post.PostCategoryLoadStateAdapter
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostCategoryChickenFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryChickenBinding>()
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
        binding = FragmentPostCategoryChickenBinding.inflate(inflater, container, false)
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
            categoryId = 8,
            exceptClose = binding.checkboxPostCategoryChickenAvailable.isChecked,
            sort = sort
        )
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter(requestManager = glideRequestManager)
        binding.rvPostCategoryChickenList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryChickenList.adapter = postCategoryListAdapter.withLoadStateFooter(
                PostCategoryLoadStateAdapter { postCategoryListAdapter.retry() }
            )
        }
    }

    private fun initSortSpinner() {
        val items = resources.getStringArray(R.array.post_category_sort_list)
        spinnerAdapter = PostCategoryListSortSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner_post_category_sort_list,
            items.toMutableList()
        )
        binding.spinnerPostCategoryChickenSort.adapter = spinnerAdapter
    }

    private fun observeSortSpinnerSelectedItem() {
        binding.spinnerPostCategoryChickenSort.onItemSelectedListener =
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
        when (binding.spinnerPostCategoryChickenSort.selectedItem) {
            getString(R.string.post_sort_createDate) -> getRecruitList(sort = "createDate")
            getString(R.string.post_sort_deadlineDate) -> getRecruitList(sort = "deadlineDate")
            getString(R.string.post_sort_score) -> getRecruitList(sort = "score")
            getString(R.string.post_sort_view) -> getRecruitList(sort = "view")
        }
    }

    private fun setDisplayAvailableRecruitPost() {
        binding.checkboxPostCategoryChickenAvailable.setOnCheckedChangeListener { _, _ ->
            setSortSpinnerRequestQuery()
        }
    }

    private fun setCategoryListContents() {
        val emptyPostCategoryViewBinding =
            ItemEmptyPostCategoryViewBinding.inflate(LayoutInflater.from(binding.root.context))
        emptyPostCategoryViewBinding.tvEmptyPostCategoryGuideNotAppear.text =
            String.format(
                getString(R.string.post_category_list_empty),
                getString(R.string.category_chicken)
            )
        val span =
            SpannableString(emptyPostCategoryViewBinding.tvEmptyPostCategoryGuideNotAppear.text)
        setEmptyViewMessage(span, getString(R.string.category_chicken))
        emptyPostCategoryViewBinding.tvEmptyPostCategoryGuideNotAppear.text = span

        val emptyView = emptyPostCategoryViewBinding.root
        addEmptyView(emptyView)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    recruitViewModel.recruitListChicken.collectLatest { recruitList ->
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
                                    constraintSet.clone(binding.layoutPostCategoryListChicken)
                                    constraintSet.setVisibility(emptyView.id, View.VISIBLE)
                                    constraintSet.applyTo(binding.layoutPostCategoryListChicken)
                                } else {
                                    constraintSet.clone(binding.layoutPostCategoryListChicken)
                                    constraintSet.setVisibility(emptyView.id, View.GONE)
                                    constraintSet.applyTo(binding.layoutPostCategoryListChicken)
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

    private fun setEmptyViewMessage(span: SpannableString, categoryName: String) {
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C)),
            span.indexOf(categoryName) - 1,
            span.indexOf(categoryName) + categoryName.length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun addEmptyView(emptyView: View) {
        binding.layoutPostCategoryListChicken.addView(emptyView)
        setConstraintLayoutCondition(emptyView.id, binding.layoutPostCategoryListChicken.id)
    }

    private fun setConstraintLayoutCondition(childLayoutId: Int, parentLayoutId: Int) {
        constraintSet.clone(binding.layoutPostCategoryListChicken)
        constraintSet.connect(
            childLayoutId,
            ConstraintSet.TOP,
            parentLayoutId,
            ConstraintSet.TOP,
            0
        )
        constraintSet.connect(
            childLayoutId,
            ConstraintSet.BOTTOM,
            parentLayoutId,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.connect(
            childLayoutId,
            ConstraintSet.START,
            parentLayoutId,
            ConstraintSet.START,
            0
        )
        constraintSet.connect(
            childLayoutId,
            ConstraintSet.END,
            parentLayoutId,
            ConstraintSet.END,
            0
        )
        constraintSet.applyTo(binding.layoutPostCategoryListChicken)
    }

    private fun setScrollToTop() {
        binding.rvPostCategoryChickenList.scrollToPosition(0)
    }
}