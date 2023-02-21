package com.mate.baedalmate.presentation.fragment.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentHistoryPostParticipatedBinding
import com.mate.baedalmate.presentation.adapter.history.HistoryPostAdapter
import com.mate.baedalmate.presentation.adapter.post.PostCategoryLoadStateAdapter
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryPostParticipatedFragment : Fragment() {
    private var binding by autoCleared<FragmentHistoryPostParticipatedBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var historyPostParticipatedAdapter: HistoryPostAdapter
    private var isGettingRecruitPostCalled: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryPostParticipatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHistoryPostParticipatedList()
        initListAdapter()
        observeHistoryPostParticipatedList()
        setRetryGetHistoryPostParticipatedList()
        setBackClickListener()
    }

    private fun setBackClickListener() {
        binding.btnHistoryPostParticipatedActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initListAdapter() {
        historyPostParticipatedAdapter = HistoryPostAdapter(requestManager = glideRequestManager)
        binding.rvHistoryPostParticipatedList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvHistoryPostParticipatedList.adapter =
                historyPostParticipatedAdapter.withLoadStateFooter(
                    PostCategoryLoadStateAdapter { historyPostParticipatedAdapter.retry() }
                )
        }

        historyPostParticipatedAdapter.setOnItemClickListener(object :
            HistoryPostAdapter.OnItemClickListener {
            override fun postClick(postId: Int, pos: Int) {
                findNavController().navigate(
                    HistoryPostParticipatedFragmentDirections.actionHistoryPostParticipatedFragmentToPostFragment(
                        postId = postId
                    )
                )
            }
        })
    }

    private fun getHistoryPostParticipatedList() {
        if (isGettingRecruitPostCalled == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    memberViewModel.requestGetHistoryPostParticipatedList(sort = "createDate")
                    isGettingRecruitPostCalled = true
                }
            }
        }
    }

    private fun observeHistoryPostParticipatedList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    memberViewModel.historyPostParticipatedList.collectLatest { recruitList ->
                        historyPostParticipatedAdapter.submitData(recruitList)
                    }
                }

                launch {
                    historyPostParticipatedAdapter.loadStateFlow.map { it.refresh }
                        .distinctUntilChanged()
                        .collectLatest { loadState -> setLoadingView(loadState) }
                }
            }
        }
    }

    private fun setRetryGetHistoryPostParticipatedList() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnHistoryPostParticipatedLoadingRetry.setOnDebounceClickListener {
                memberViewModel.requestGetHistoryPostParticipatedList(sort = "createDate")
            }
        }
    }

    private fun setLoadingView(loadState: LoadState) {
        with(binding) {
            lottieHistoryPostParticipatedLoading.isVisible =
                loadState is LoadState.Loading
            btnHistoryPostParticipatedLoadingRetry.isVisible =
                loadState is LoadState.Error
            tvHistoryPostParticipatedLoadingErrorGuide.isVisible =
                loadState is LoadState.Error
            rvHistoryPostParticipatedList.isVisible =
                loadState is LoadState.NotLoading
        }
    }
}