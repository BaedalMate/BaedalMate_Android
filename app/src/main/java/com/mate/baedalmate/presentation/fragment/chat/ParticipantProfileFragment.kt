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
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParticipantProfileFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentParticipantProfileBinding>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private val blockViewModel by activityViewModels<BlockViewModel>()
    private val args by navArgs<ParticipantProfileFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var blockAlertDialog: AlertDialog
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return initBottomSheetDialog()
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
        initBlockDialog()
        setReportClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BlockAlertDialog.hideBlockDialog(blockAlertDialog)
    }

    private fun initBottomSheetDialog(): BottomSheetDialog {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRadius)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.behavior.skipCollapsed = true // Dialog가 길어지는 경우 Half_expand되는 경우 방지
        return bottomSheetDialog
    }

    private fun setUserInfo() {
        args.participant?.let { participantInfo ->
            glideRequestManager.load("http://3.35.27.107:8080/images/${participantInfo.profileImage}")
                .thumbnail(0.1f)
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgParticipantProfileThumbnail)
            binding.tvParticipantProfileNickname.text = participantInfo.nickname
            setBlockClickListener(isBlockedUser = participantInfo.block)
        }
    }

    private fun setReportClickListener() {
        binding.layoutParticipantProfileActionReport.setOnDebounceClickListener {
            args.participant?.let { participantInfo ->
                findNavController().navigate(
                    ParticipantProfileFragmentDirections.actionParticipantProfileFragmenttToReportUserFragment(
                        userId = participantInfo.userId,
                        userName = participantInfo.nickname
                    )
                )
            }
        }
    }

    private fun initBlockDialog() {
        blockAlertDialog = BlockAlertDialog.createBlockDialog(
            requireContext(),
            "${args.participant?.nickname} 님을 차단하시겠습니까?",
            getString(R.string.block_dialog_description)
        ) { findNavController().navigateUp() }
    }

    private fun setBlockClickListener(isBlockedUser: Boolean) {
        if (isBlockedUser) {
            binding.tvParticipantProfileActionBlock.text = getString(R.string.unblock)
            binding.layoutParticipantProfileActionBlock.setOnDebounceClickListener {
                blockAlertDialog = BlockAlertDialog.createBlockDialog(
                    requireContext(),
                    getString(R.string.unblock_dialog_title),
                    getString(R.string.unblock_dialog_description),
                ) {
                    unblockUser()
                    observeUnBlockUserSuccess()
                }

                BlockAlertDialog.showBlockDialog(blockAlertDialog)
                BlockAlertDialog.resizeDialogFragment(
                    requireContext(),
                    blockAlertDialog,
                    dialogSizeRatio = 0.8f
                )
            }
        } else {
            binding.tvParticipantProfileActionBlock.text = getString(R.string.block)
            binding.layoutParticipantProfileActionBlock.setOnDebounceClickListener {
                blockAlertDialog = BlockAlertDialog.createBlockDialog(
                    requireContext(),
                    "${args.participant?.nickname} 님을 차단하시겠습니까?",
                    getString(R.string.block_dialog_description)
                ) {
                    blockUser()
                    observeBlockUserSuccess()
                }

                BlockAlertDialog.showBlockDialog(blockAlertDialog)
                BlockAlertDialog.resizeDialogFragment(
                    requireContext(),
                    blockAlertDialog,
                    dialogSizeRatio = 0.8f
                )
            }
        }
    }

    private fun blockUser() {
        args.participant?.let { participantInfo ->
            blockViewModel.requestPostBlockUser(blockUserId = participantInfo.userId)
        }
    }

    private fun observeBlockUserSuccess() {
        blockViewModel.isSuccessBlockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_success_toast_message),
                        "${args.participant?.nickname}"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isSuccess == false) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.block_user_block_fail_unknown_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            chatViewModel.getChatParticipants(args.recruitId)
            findNavController().navigateUp()
        }
        blockViewModel.isAlreadyBlockedUser.observe(viewLifecycleOwner) { isAlreadyBlocked ->
            if (isAlreadyBlocked == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_fail_already_blocked_toast_message),
                        "${args.participant?.nickname}"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            chatViewModel.getChatParticipants(args.recruitId)
            findNavController().navigateUp()
        }
    }

    private fun unblockUser() {
        args.participant?.let { participantInfo ->
            blockViewModel.requestPostUnblockUser(blockedUserId = participantInfo.userId)
        }
    }

    private fun observeUnBlockUserSuccess() {
        blockViewModel.isSuccessUnblockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_unblock_success_toast_message),
                        "${args.participant?.nickname}"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isSuccess == false) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.block_user_unblock_fail_unknown_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            chatViewModel.getChatParticipants(args.recruitId)
            findNavController().navigateUp()
        }
    }
}