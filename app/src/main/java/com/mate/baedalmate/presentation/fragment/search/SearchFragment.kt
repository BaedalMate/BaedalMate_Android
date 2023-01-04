package com.mate.baedalmate.presentation.fragment.search

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.HideKeyBoardUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentSearchBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import com.mate.baedalmate.presentation.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchBinding>()
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var searchResultListAdapter: PostCategoryListAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setSearchClickListener()
        initListAdapter()
        setSearchResultListContents()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchViewModel.clearSearchKeywordResult()
    }

    private fun setBackClickListener() {
        binding.btnSearchActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setSearchClickListener() {
        btnTextSearchClickListener()
        editTextSearchClickListener()
    }

    private fun btnTextSearchClickListener() {
        binding.btnSearchActionbarAction.setOnDebounceClickListener {
            getSearchResultList(keyword = binding.etSearchActionbarKeyword.text.toString())
            HideKeyBoardUtil.hideEditText(
                requireContext(),
                binding.etSearchActionbarKeyword
            )
        }
    }

    private fun editTextSearchClickListener() {
        binding.etSearchActionbarKeyword.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    if (binding.etSearchActionbarKeyword.text.toString().trim().isNotEmpty()) {
                        getSearchResultList(keyword = binding.etSearchActionbarKeyword.text.toString())
                    }
                    HideKeyBoardUtil.hideEditText(
                        requireContext(),
                        binding.etSearchActionbarKeyword
                    )
                    false
                }
                else -> false
            }
        }
    }

    private fun getSearchResultList(keyword: String, sort: String = "deadlineDate") {
        // TODO 검색시 무한 스크롤로 내릴 수 있도록 처리해야함
        if (keyword.trim().isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.search_result_empty_keyword_toast_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            searchViewModel.requestSearchKeywordResult(
                keyword = keyword,
                sort = sort
            )
        }
    }

    private fun initListAdapter() {
        searchResultListAdapter = PostCategoryListAdapter(requestManager = glideRequestManager)
        binding.rvSearchResultList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvSearchResultList.adapter = searchResultListAdapter
        }
    }

    private fun setSearchResultListContents() {
        setEmptyViewNew()
        searchViewModel.searchKeywordResults.observe(viewLifecycleOwner) { searchResultList ->
            binding.tvSearchResultCount.text =
                String.format(
                    getString(R.string.search_result_count),
                    searchResultList.recruitList.size
                )
            val span = SpannableString(binding.tvSearchResultCount.text)
            setHighlightSpanMessage(span, searchResultList.recruitList.size.toString())
            binding.tvSearchResultCount.text = span

            if (searchResultList.recruitList.isNotEmpty()) {
                binding.layoutSearchResultEmpty.visibility = View.GONE
            } else if (binding.etSearchActionbarKeyword.text.isNotEmpty()) {
                setEmptyViewNotAppear()
                binding.layoutSearchResultEmpty.visibility = View.VISIBLE
            } else { // 아무것도 입력하지 않은 경우
                binding.layoutSearchResultEmpty.visibility = View.GONE
            }

            searchResultListAdapter.submitList(searchResultList.recruitList.toMutableList())
        }

        searchResultListAdapter.setOnItemClickListener(object :
            PostCategoryListAdapter.OnItemClickListener {
            override fun postClick(postId: Int, pos: Int) {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToPostFragment(
                        postId = postId
                    )
                )
            }
        })
    }

    private fun setEmptyViewNotAppear() {
        binding.tvSearchResultEmptyNotAppear.text =
            String.format(
                getString(R.string.search_result_empty_not_appear),
                binding.etSearchActionbarKeyword.text
            )

        val span = SpannableString(binding.tvSearchResultEmptyNotAppear.text)
        setHighlightSpanMessage(
            span,
            binding.etSearchActionbarKeyword.text.toString(),
            endExtraPosition = 1
        )
        binding.tvSearchResultEmptyNotAppear.text = span
    }

    private fun setEmptyViewNew() {
        binding.btnSearchResultEmptyNew.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_writeCategoryFragment)
        }
    }

    private fun setHighlightSpanMessage(
        span: SpannableString,
        highlightMessage: String,
        startPosition: Int = 0,
        endExtraPosition: Int = 1
    ) {
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C)),
            startPosition,
            span.indexOf(highlightMessage) + highlightMessage.length + endExtraPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}