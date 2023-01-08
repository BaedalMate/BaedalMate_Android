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
import com.mate.baedalmate.databinding.FragmentHistoryPostParticipatedBinding
import com.mate.baedalmate.presentation.adapter.history.HistoryPostAdapter
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryPostParticipatedFragment : Fragment() {
    private var binding by autoCleared<FragmentHistoryPostParticipatedBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var historyPostParticipatedAdapter: HistoryPostAdapter

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
        initListAdapter()
        getHistoryPostParticipatedList()
        observeHistoryPostParticipatedList()
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
            rvHistoryPostParticipatedList.adapter = historyPostParticipatedAdapter
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memberViewModel.requestGetHistoryPostParticipatedList(
                    page = 0,
                    size = 10000,
                    sort = "deadlineDate|asc"
                )
            }
        }
    }

    private fun observeHistoryPostParticipatedList() {
        viewLifecycleOwner.lifecycleScope.launch {
            memberViewModel.historyPostParticipatedList.observe(viewLifecycleOwner) { participatedPostList ->
                historyPostParticipatedAdapter.submitList(participatedPostList.recruitList.toMutableList())
            }
        }
    }
}