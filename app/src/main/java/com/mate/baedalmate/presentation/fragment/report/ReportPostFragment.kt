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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.ReportAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentReportPostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportPostFragment : Fragment() {
    private var binding by autoCleared<FragmentReportPostBinding>()
    private val args by navArgs<ReportPostFragmentArgs>()
    private lateinit var reportSubmitAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportSubmitAlertDialog = ReportAlertDialog.createReportDialog(
            requireContext(),
            { findNavController().navigateUp() }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setUserName()
        setReasonSelectClickListener()
        setReportUserClickListener()
        setReportPostSubmitClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ReportAlertDialog.hideReportDialog(reportSubmitAlertDialog)
    }

    private fun setBackClickListener() {
        binding.btnReportPostActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUserName() {
        binding.tvReportPostNavigateUserTitle.text =
            String.format(getString(R.string.report_post_navigate_user_title), args.postWriterName)
    }

    private fun setReasonSelectClickListener() {
        binding.radiogroupReportPostReason.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_report_post_reason_etc -> {
                    binding.etReportPostReasonEtc.visibility = View.VISIBLE
                }
                else -> {
                    binding.etReportPostReasonEtc.visibility = View.GONE
                }
            }

            // TODO selector XML 활용해서 변경하도록 설정
            for (childView in binding.radiogroupReportPostReason.children) {
                if (childView is RadioButton) {
                    (childView as RadioButton).buttonTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.gray_dark_666666)
                }
            }
            requireView().findViewById<RadioButton>(checkedId).buttonTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.main_FB5F1C)

        }
    }

    private fun setReportUserClickListener() {
        binding.tvReportPostNavigateUserDescription.setOnDebounceClickListener {
            findNavController().navigate(
                ReportPostFragmentDirections.actionReportPostFragmentToReportUserFragment(
                    userId = args.postWriterUserId,
                    userName = args.postWriterName
                )
            )
        }
    }

    private fun setReportPostSubmitClickListener() {
        binding.btnReportPostSubmit.setOnDebounceClickListener {
            if (binding.radiogroupReportPostReason.checkedRadioButtonId == R.id.radiobutton_report_post_reason_etc &&
                binding.etReportPostReasonEtc.text.trim().isEmpty()
            ) {
                Toast.makeText(requireContext(), getString(R.string.report_reason_etc_empty_toast_message), Toast.LENGTH_SHORT).show()
            } else {
                showSubmitCompleteDialog()

                // args.postId
                // TODO
            }
        }
    }

    private fun showSubmitCompleteDialog() {
        ReportAlertDialog.showReportDialog(reportSubmitAlertDialog)
        ReportAlertDialog.resizeDialogFragment(
            requireContext(),
            reportSubmitAlertDialog,
            dialogSizeRatio = 0.8f
        )
    }
}