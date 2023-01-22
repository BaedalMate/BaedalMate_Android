package com.mate.baedalmate.presentation.fragment.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentHistoryPostCreatedBinding
import com.mate.baedalmate.presentation.adapter.history.HistoryPostAdapter
import com.mate.baedalmate.presentation.adapter.post.PostCategoryLoadStateAdapter
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryPostCreatedFragment : Fragment() {
    private var binding by autoCleared<FragmentHistoryPostCreatedBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var historyPostCreatedAdapter: HistoryPostAdapter
    private var isGettingRecruitPostCalled: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryPostCreatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListAdapter()
        getHistoryPostCreatedList()
        observeHistoryPostCreatedList()
        setBackClickListener()
    }

    private fun setBackClickListener() {
        binding.btnHistoryPostCreatedActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initListAdapter() {
        historyPostCreatedAdapter = HistoryPostAdapter(requestManager = glideRequestManager)
        binding.rvHistoryPostCreatedList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvHistoryPostCreatedList.adapter = historyPostCreatedAdapter.withLoadStateFooter(
                PostCategoryLoadStateAdapter { historyPostCreatedAdapter.retry() }
            )
        }

        historyPostCreatedAdapter.setOnItemClickListener(object :
            HistoryPostAdapter.OnItemClickListener {
            override fun postClick(postId: Int, pos: Int) {
                findNavController().navigate(
                    HistoryPostCreatedFragmentDirections.actionHistoryPostCreatedFragmentToPostFragment(
                        postId = postId
                    )
                )
            }
        })
    }

    private fun getHistoryPostCreatedList() {
        if (isGettingRecruitPostCalled == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    memberViewModel.requestGetHistoryPostCreatedList(sort = "createDate")
                    isGettingRecruitPostCalled = true
                }
            }
        }
    }

    private fun observeHistoryPostCreatedList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    memberViewModel.historyPostCreatedList.collectLatest { recruitList ->
                        historyPostCreatedAdapter.submitData(recruitList)
                    }
                }
            }
        }
    }
}