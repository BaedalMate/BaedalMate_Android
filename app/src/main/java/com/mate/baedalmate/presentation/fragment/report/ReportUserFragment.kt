package com.mate.baedalmate.presentation.fragment.report

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.ReportAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentReportUserBinding
import com.mate.baedalmate.presentation.viewmodel.BlockViewModel
import com.mate.baedalmate.presentation.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportUserFragment : Fragment() {
    private var binding by autoCleared<FragmentReportUserBinding>()
    private val blockViewModel by activityViewModels<BlockViewModel>()
    private val reportViewModel by activityViewModels<ReportViewModel>()
    private val args by navArgs<ReportUserFragmentArgs>()
    private lateinit var reportSubmitAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportSubmitAlertDialog = ReportAlertDialog.createReportDialog(
            requireContext(),
            getString(R.string.report_dialog_title),
            String.format(
                getString(R.string.report_user_description),
                args.userName
            ),
            cancelButtonFunction = { findNavController().navigateUp() },
            confirmButtonFunction = { findNavController().navigateUp() }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setUserName()
        setReasonSelectClickListener()
        setReportUserSubmitClickListener()
        observeReportSubmitSuccess()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ReportAlertDialog.hideReportDialog(reportSubmitAlertDialog)
    }

    private fun setBackClickListener() {
        binding.btnReportUserActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUserName() {
        binding.tvReportUserTitle.text =
            String.format(getString(R.string.report_user_title), args.userName)
    }

    private fun setReasonSelectClickListener() {
        binding.radiogroupReportUserReason.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_report_user_reason_etc -> {
                    binding.etReportUserReasonEtc.visibility = View.VISIBLE
                }
                else -> {
                    binding.etReportUserReasonEtc.visibility = View.GONE
                }
            }

            // TODO selector XML 활용해서 변경하도록 설정
            for (childView in binding.radiogroupReportUserReason.children) {
                if (childView is RadioButton) {
                    (childView as RadioButton).buttonTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.gray_dark_666666)
                }
            }
            requireView().findViewById<RadioButton>(checkedId).buttonTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.main_FB5F1C)

        }
    }

    private fun setReportUserSubmitClickListener() {
        binding.btnReportUsersubmit.setOnDebounceClickListener {
            if (binding.radiogroupReportUserReason.checkedRadioButtonId == R.id.radiobutton_report_user_reason_etc &&
                binding.etReportUserReasonEtc.text.trim().isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.report_reason_etc_empty_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.radiogroupReportUserReason.checkedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.report_submit_fail_select_reason_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                reportViewModel.requestPostReportUser(
                    targetUserId = args.userId,
                    reportReason = requireView().findViewById<RadioButton>(binding.radiogroupReportUserReason.checkedRadioButtonId).text.toString(),
                    reportDetail = binding.etReportUserReasonEtc.text.trim().toString()
                )
            }
        }
    }

    private fun observeReportSubmitSuccess() {
        reportViewModel.isSuccessReportUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                showSubmitCompleteDialog()
                observeBlockUserResult()
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.report_submit_fail_network_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }

        reportViewModel.isDuplicatedReportUser.observe(viewLifecycleOwner) { isDuplicated ->
            if (isDuplicated.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.report_submit_fail_duplicated_user_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun showSubmitCompleteDialog() {
        reportSubmitAlertDialog = ReportAlertDialog.createReportDialog(
            requireContext(),
            getString(R.string.report_dialog_title),
            String.format(
                getString(R.string.report_user_description),
                args.userName
            ),
            cancelButtonFunction = { findNavController().navigateUp() },
            confirmButtonFunction = { blockViewModel.requestPostBlockUser(blockUserId = args.userId) })

        ReportAlertDialog.showReportDialog(reportSubmitAlertDialog)
        ReportAlertDialog.resizeDialogFragment(
            requireContext(),
            reportSubmitAlertDialog,
            dialogSizeRatio = 0.8f
        )
    }

    private fun observeBlockUserResult() {
        blockViewModel.isSuccessBlockUser.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    String.format(
                        getString(R.string.block_user_block_success_toast_message),
                        args.userName
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }
}