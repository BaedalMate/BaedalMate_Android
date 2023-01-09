package com.mate.baedalmate.presentation.fragment.chat

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.BlockAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentParticipantProfileBinding
import com.mate.baedalmate.presentation.viewmodel.BlockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParticipantProfileFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentParticipantProfileBinding>()
    private val blockViewModel by activityViewModels<BlockViewModel>()
    private val args by navArgs<ParticipantProfileFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var blockAlertDialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRadius)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserInfo()
        setBlockClickListener()
        setReportClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BlockAlertDialog.hideBlockDialog(blockAlertDialog)
    }

    private fun setUserInfo() {
        glideRequestManager.load("http://3.35.27.107:8080/images/${args.participant.profileImage}")
            .thumbnail(0.1f)
            .priority(Priority.HIGH)
            .centerCrop()
            .into(binding.imgParticipantProfileThumbnail)
        binding.tvParticipantProfileNickname.text = args.participant.nickname
    }

    private fun setReportClickListener() {
        binding.layoutParticipantProfileActionReport.setOnDebounceClickListener {
            findNavController().navigate(
                ParticipantProfileFragmentDirections.actionParticipantProfileFragmenttToReportUserFragment(
                    userId = args.participant.userId,
                    userName = args.participant.nickname
                )
            )
        }
    }

    private fun setBlockClickListener() {
        binding.layoutParticipantProfileActionBlock.setOnDebounceClickListener {
            blockAlertDialog = BlockAlertDialog.createBlockDialog(
                requireContext(),
                {
                    blockUser()
                    observeBlockUserSuccess()
                },
                args.participant.nickname
            )

            BlockAlertDialog.showBlockDialog(blockAlertDialog)
            BlockAlertDialog.resizeDialogFragment(
                requireContext(),
                blockAlertDialog,
                dialogSizeRatio = 0.8f
            )
        }
    }

    private fun blockUser() {
        blockViewModel.requestPostBlockUser(blockUserId = args.participant.userId)
    }

    private fun observeBlockUserSuccess() {
        blockViewModel.isSuccessBlockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_success_toast_message),
                        "${args.participant.nickname}"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.block_user_block_fail_unknown_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            findNavController().navigateUp()
        }
        blockViewModel.isAlreadyBlockedUser.observe(viewLifecycleOwner) { isAlreadyBlocked ->
            if (isAlreadyBlocked.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_fail_already_blocked_toast_message),
                        "${args.participant.nickname}"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            findNavController().navigateUp()
        }
    }

    private fun unblockUser() { // TODO 차단여부 서버에서 받아올 수 있는 경우 차단해제를 같은 위치에 보여줄 수 있도록 구현해야함
        blockViewModel.requestPostUnblockUser(blockedUserId = args.participant.userId)
    }

    private fun observeUnBlockUserSuccess() {
        blockViewModel.isSuccessUnblockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_unblock_success_toast_message),
                        "${args.participant.nickname}"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.block_user_unblock_fail_unknown_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            findNavController().navigateUp()
        }
    }
}