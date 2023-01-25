package com.mate.baedalmate.presentation.fragment.block

import android.app.AlertDialog
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
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.BlockAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentBlockUserListBinding
import com.mate.baedalmate.presentation.adapter.block.BlockedUserListAdapter
import com.mate.baedalmate.presentation.viewmodel.BlockViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlockUserListFragment : Fragment() {
    private var binding by autoCleared<FragmentBlockUserListBinding>()
    private val blockViewModel by activityViewModels<BlockViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var blockedUserListAdapter: BlockedUserListAdapter
    private lateinit var unblockAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlockUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUnblockDialog()
        setReviewUserListAdapter()
        getBlockedUserList()
        observeBlockedUserList()
        observeUnblockResponse()
        setBackClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BlockAlertDialog.hideBlockDialog(unblockAlertDialog)
    }


    private fun initUnblockDialog() {
        unblockAlertDialog = BlockAlertDialog.createBlockDialog(
            requireContext(),
            getString(R.string.unblock_dialog_title),
            getString(R.string.unblock_dialog_description)
        ) { findNavController().navigateUp() }
    }

    private fun setReviewUserListAdapter() {
        blockedUserListAdapter = BlockedUserListAdapter(requestManager = glideRequestManager)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        with(binding.rvBlockUserList) {
            this.layoutManager = layoutManager
            this.adapter = blockedUserListAdapter
        }
        setUnblockClickListener()
    }

    private fun setUnblockClickListener() {
        blockedUserListAdapter.setOnItemClickListener(object :
            BlockedUserListAdapter.OnItemClickListener {
            override fun unblockBlockedUser(userId: Int, userName: String, pos: Int) {
                unblockAlertDialog = BlockAlertDialog.createBlockDialog(
                    requireContext(),
                    getString(R.string.unblock_dialog_title),
                    getString(R.string.unblock_dialog_description),
                ) { unblockUser(blockedUserId = userId) }

                BlockAlertDialog.showBlockDialog(unblockAlertDialog)
                BlockAlertDialog.resizeDialogFragment(
                    requireContext(),
                    unblockAlertDialog,
                    dialogSizeRatio = 0.8f
                )
            }
        })
    }

    private fun unblockUser(blockedUserId: Int) {
        blockViewModel.requestPostUnblockUser(blockedUserId = blockedUserId)
    }

    private fun setBackClickListener() {
        binding.btnBlockUserListActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getBlockedUserList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                blockViewModel.requestGetBlockedUserList()
            }
        }
    }

    private fun observeBlockedUserList() {
        blockViewModel.blockedUserList.observe(viewLifecycleOwner) { response ->
            blockedUserListAdapter.submitList(response.blockList.toMutableList())
            binding.tvBlockUserListEmpty.visibility = if (response.blockList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeUnblockResponse() {
        blockViewModel.isSuccessUnblockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                getBlockedUserList()
            }
        }
    }
}