package com.mate.baedalmate.presentation.fragment.review

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentReviewUserBinding
import com.mate.baedalmate.domain.model.UserDto
import com.mate.baedalmate.presentation.adapter.review.ReviewUserAdapter
import com.mate.baedalmate.presentation.viewmodel.ReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewUserFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentReviewUserBinding>()
    private val args by navArgs<ReviewUserFragmentArgs>()
    private val reviewViewModel by activityViewModels<ReviewViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var reviewUserAdapter: ReviewUserAdapter
    private var reviewedUserList: MutableList<UserDto> = mutableListOf<UserDto>()
    private lateinit var loadingToastMessage: Toast

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRadius)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
        loadingToastMessage =
            Toast.makeText(requireContext(), R.string.review_submit_toast_loading, Toast.LENGTH_SHORT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getReviewTargetUserList()
        setSubmitClickListener()
        setReviewUserListAdapter()
        setMenuListOriginalValue()
    }

    override fun onDestroy() {
        loadingToastMessage.cancel()
        super.onDestroy()
    }

    private fun getReviewTargetUserList() {
        reviewViewModel.requestGetTargetReviewUserList(recruitId = args.recruitId)
    }

    private fun setMenuListOriginalValue() {
        reviewViewModel.reviewTargetUserList.observe(viewLifecycleOwner) {
            val participants = it.participants
            for (participant in participants) {
                reviewedUserList.add(UserDto(5.0f, participant.userId))
            }

            if (!participants.isNullOrEmpty()) {
                reviewUserAdapter.submitList(participants.toMutableList())
            }
        }
    }

    private fun setReviewUserListAdapter() {
        reviewUserAdapter = ReviewUserAdapter(requestManager = glideRequestManager)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        with(binding.rvReviewUserList) {
            this.layoutManager = layoutManager
            this.adapter = reviewUserAdapter
        }
        setUserStarClickListener()
    }

    private fun setUserStarClickListener() {
        reviewUserAdapter.setOnItemClickListener(object :
            ReviewUserAdapter.OnItemClickListener {
            override fun setUserScore(score: Float, pos: Int) {
                reviewedUserList[pos].score = score
            }
        })
    }

    private fun setSubmitClickListener() {
        binding.btnReviewUserSubmit.setOnDebounceClickListener {
            loadingToastMessage.show()

            reviewViewModel.requestCreateReviewUsers(
                recruitId = args.recruitId,
                participatedUsers = reviewedUserList
            )
            findNavController().navigateUp()
        }
    }
}