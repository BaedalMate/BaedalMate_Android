package com.mate.baedalmate.presentation.fragment.post

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.BlockAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentPostOptionBinding
import com.mate.baedalmate.presentation.viewmodel.BlockViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentPostOptionBinding>()
    private val args by navArgs<PostOptionFragmentArgs>()
    private val blockViewModel by activityViewModels<BlockViewModel>()
    private lateinit var blockAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostOptionBinding.inflate(inflater, container, false)
        initDialogFragmentLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBlockDialog()
        setBlockClickListener()
        setReportClickListener()
    }

    override fun onResume() {
        super.onResume()
        resizeOptionDialogFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BlockAlertDialog.hideBlockDialog(blockAlertDialog)
    }

    private fun initDialogFragmentLayout() {
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().window?.setGravity(Gravity.BOTTOM)
    }

    private fun resizeOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = GetDeviceSize.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun initBlockDialog() {
        blockAlertDialog = BlockAlertDialog.createBlockDialog(
            requireContext(), "${args.postWriterName} 님을 차단하시겠습니까?",
            getString(R.string.block_dialog_description)
        ) {
            blockUser()
            observeBlockUserSuccess()
        }
    }

    private fun setBlockClickListener() {
        binding.layoutPostOptionSelectBlock.setOnDebounceClickListener {
            BlockAlertDialog.showBlockDialog(blockAlertDialog)
            BlockAlertDialog.resizeDialogFragment(
                requireContext(),
                blockAlertDialog,
                dialogSizeRatio = 0.8f
            )
        }
    }

    private fun setReportClickListener() {
        binding.layoutPostOptionSelectReport.setOnDebounceClickListener {
            findNavController().navigate(
                PostOptionFragmentDirections.actionPostOptionFragmentToReportPostFragment(
                    postId = args.postId,
                    postWriterUserId = args.postWriterUserId,
                    postWriterName = args.postWriterName
                )
            )
        }
    }
    private fun blockUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                blockViewModel.requestPostBlockUser(blockUserId = args.postWriterUserId)
            }
        }
    }

    private fun observeBlockUserSuccess() {
        blockViewModel.isSuccessBlockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_success_toast_message),
                        args.postWriterName
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
            findNavController().navigateUp()
        }

        blockViewModel.isAlreadyBlockedUser.observe(viewLifecycleOwner) { isAlreadyBlocked ->
            if (isAlreadyBlocked == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_fail_already_blocked_toast_message),
                        args.postWriterName
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            findNavController().navigateUp()
        }
    }
}