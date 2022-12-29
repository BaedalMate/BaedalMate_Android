package com.mate.baedalmate.presentation.fragment.search

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentSearchBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchBinding>()
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
        getSearchResultList()
        initListAdapter()
        setSearchResultListContents()
    }

    private fun getSearchResultList() {
        // TODO
    }

    private fun initListAdapter() {
        searchResultListAdapter = PostCategoryListAdapter(requestManager = glideRequestManager)
        binding.rvSearchResultList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvSearchResultList.adapter = searchResultListAdapter
        }
    }

    private fun setSearchResultListContents() {
        setEmptyView()
//        recruitViewModel.recruitListAsia.observe(viewLifecycleOwner) { searchResultList ->
//            binding.tvSearchResultCount.text =
//                String.format(
//                    getString(R.string.search_result_count),
//                    searchResultList.size
//                )
//            val span = SpannableString(binding.tvSearchResultCount.text)
//            setHighlightSpanMessage(span, searchResultList.size.toString())
//            binding.tvSearchResultCount.text = span
//
//            if (true) { // recruitList.recruitList.isNotEmpty()
//                searchResultListAdapter.submitList(emptyList()) // TODO
//                binding.layoutSearchResultEmpty.visibility = View.VISIBLE
//            } else {
//                binding.layoutSearchResultEmpty.visibility = View.GONE
//            }
//        }

//        searchResultListAdapter.setOnItemClickListener(object :
//            PostCategoryListAdapter.OnItemClickListener {
//            override fun postClick(postId: Int, pos: Int) {
//                findNavController().navigate(
//                    SearchFragmentDirections.actionSearchFragmentToPostFragment(
//                        postId = postId
//                    )
//                )
//            }
//        })
    }

    private fun setEmptyView() {
        setEmptyViewNotAppear()
        setEmptyViewNew()
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
            endExtraPosition = 2
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