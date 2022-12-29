package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentPostCategoryChickenBinding
import com.mate.baedalmate.databinding.ItemEmptyPostCategoryViewBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryChickenFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryChickenBinding>()
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
        binding = FragmentPostCategoryChickenBinding.inflate(inflater, container, false)
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
            categoryId = 8,
            page = 0,
            size = 25,
            sort = sort
        )
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter(requestManager = glideRequestManager)
        binding.rvPostCategoryChickenList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryChickenList.adapter = postCategoryListAdapter
        }
    }

    private fun setCategoryClickListener() {
        binding.radiogroupLayoutPostCategoryChickenSort.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_post_category_chicken_sort_time -> {
                    getRecruitList(sort = "deadlineDate")
                }
                R.id.radiobutton_post_category_chicken_sort_star -> {
                    getRecruitList(sort = "score")
                }
                R.id.radiobutton_post_category_chicken_sort_popular -> {
                    getRecruitList(sort = "view")
                }
            }
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

        recruitViewModel.recruitListChicken.observe(viewLifecycleOwner) { recruitList ->
            if (recruitList.recruitList.isNotEmpty()) {
                postCategoryListAdapter.submitList(recruitList.recruitList.toMutableList())
                with(constraintSet) {
                    clone(binding.layoutPostCategoryListChicken)
                    setVisibility(emptyView.id, View.GONE)
                    applyTo(binding.layoutPostCategoryListChicken)
                }
            } else {
                with(constraintSet) {
                    clone(binding.layoutPostCategoryListChicken)
                    setVisibility(emptyView.id, View.VISIBLE)
                    applyTo(binding.layoutPostCategoryListChicken)
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
}