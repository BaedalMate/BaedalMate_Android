package com.mate.baedalmate.presentation.fragment.write

import android.app.AlertDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.LoadingAlertDialog
import com.mate.baedalmate.common.extension.addSourceList
import com.mate.baedalmate.common.extension.navigateSafe
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentWriteFourthBinding
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.presentation.adapter.write.WriteFourthMenuListAdapter
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class WriteFourthFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFourthBinding>()
    private val args by navArgs<WriteFirstFragmentArgs>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var writeFourthMenuListAdapter: WriteFourthMenuListAdapter
    private var currentMenuAmount = 0
    private val decimalFormat = DecimalFormat("#,###")
    private lateinit var loadingAlertDialog: AlertDialog

    private val _isMenuAdded = MutableLiveData<Boolean>()
    private val _isPassedAmountLimit = MutableLiveData<Boolean>()
    val isNextEnable = MediatorLiveData<Boolean>().apply {
        addSourceList(
            _isMenuAdded,
            _isPassedAmountLimit
        ) { _isNextEnable() }
    }

    private fun _isNextEnable(): Boolean {
        if ((_isMenuAdded.value == true) and (_isPassedAmountLimit.value == true)) {
            return true
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFourthBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setNextClickListener()
        setAddMenuClickListener()
        setMenuListOriginalValue()
        initDetailForModify()
        observeUploadSuccess()
        observeModifySuccess()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun setBackClickListener() {
        binding.btnWriteFourthActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        isNextEnable.observe(viewLifecycleOwner) { isEnable ->
            binding.btnWriteFourthNext.isEnabled = isEnable
        }

        binding.btnWriteFourthNext.setOnDebounceClickListener {
            if (writeViewModel.deadLineAmount <=
                binding.tvWriteFourthAmountTotal.text.toString().replace(",", "").replace("원", "")
                    .toInt()
            ) {
                Toast.makeText(
                    requireContext(),
                    "목표 금액 ${decimalFormat.format(writeViewModel.deadLineAmount)}원 보다 결제 예정금액이 작아야 합니다",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                if (args.recruitDetailForModify != null)
                    args.recruitDetailForModify?.let {
                        writeViewModel.requestModifyPost(it.recruitId)
                    }
                else writeViewModel.requestUploadPost()
                LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog)
            }
        }
    }

    private fun setAddMenuClickListener() {
        binding.btnWriteFourthMenuAdd.setOnDebounceClickListener {
            findNavController().navigateSafe(R.id.action_writeFourthFragment_to_writeFourthAddMenuFragment)
        }
    }

    private fun setMenuListOriginalValue() {
        writeViewModel.menuList.observe(viewLifecycleOwner) {
            setMenuListAdapter(menuList = it)

            if (!it.isNullOrEmpty()) {
                writeFourthMenuListAdapter.notifyItemInserted(0)
                _isMenuAdded.postValue(true)
            } else {
                _isMenuAdded.postValue(false)
            }
            setMenuDeleteClickListener()
            setMenuTotalAmount(menuList = it)
        }
    }

    private fun setMenuListAdapter(menuList: MutableList<MenuDto>) {
        writeFourthMenuListAdapter = WriteFourthMenuListAdapter(menuList)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false).apply {
                reverseLayout = true
                stackFromEnd = true
            }
        with(binding.rvWriteFourthAddList) {
            this.layoutManager = layoutManager
            this.adapter = writeFourthMenuListAdapter
        }
    }

    private fun setMenuDeleteClickListener() {
        writeFourthMenuListAdapter.setOnItemClickListener(object :
            WriteFourthMenuListAdapter.OnItemClickListener {
            override fun deleteMenu(contents: MenuDto, pos: Int) {
                binding.rvWriteFourthAddList[pos].animate().alpha(0f).setDuration(100L)
                    .withEndAction {
                        writeViewModel.menuList.removeAt(pos)
                        writeFourthMenuListAdapter.notifyItemRemoved(pos)
                    }
            }
        })
    }

    private fun setMenuTotalAmount(menuList: MutableList<MenuDto>) {
        var result = ""

        currentMenuAmount = 0 // 메뉴 총 금액
        for (menu in menuList) {
            val menuAmount = menu.price
            currentMenuAmount += (menuAmount * menu.quantity)
        }

        result = decimalFormat.format(currentMenuAmount.toString().replace(",", "").toDouble())
        binding.tvWriteFourthAmountDetailTotalCurrent.text = String.format(getString(R.string.unit_korea_with_money), result)

        val currentDeliveryFee = writeViewModel.deliveryFee // 배달비 설정
        binding.tvWriteFourthAmountDetailDeliveryFeeCurrent.text =
            "+ ${
                String.format(
                    getString(R.string.unit_korea_with_money),
                    decimalFormat.format(currentDeliveryFee)
                )
            }"

        val totalAmount = decimalFormat.format(
            (currentMenuAmount + currentDeliveryFee).toString().replace(",", "").toDouble()
        )
        binding.tvWriteFourthAmountDetailTotal.text =
            String.format(getString(R.string.unit_korea_with_money), totalAmount)
        setMenuTotalAmountIsError(currentMenuAmount)
        setTotalAmount(totalAmount.replace(",", "").toInt())
    }

    private fun setMenuTotalAmountIsError(menuAmount: Int) {
        val isPassedLimit = writeViewModel.deadLineAmount > menuAmount

        _isPassedAmountLimit.postValue(isPassedLimit)
        with(binding.tvWriteFourthMenuAddError) {
            text = String.format(
                getString(R.string.write_fourth_menu_add_error),
                decimalFormat.format(writeViewModel.deadLineAmount)
            )
            animate().alpha(if (isPassedLimit) 0f else 1f).setDuration(200L)
        }
    }

    private fun setTotalAmount(totalAmount: Int) {
        binding.tvWriteFourthAmountTotal.text = String.format(
            getString(R.string.unit_korea_with_money),
            decimalFormat.format(totalAmount)
        ) // 최종 결제금액
        val span = SpannableString(binding.tvWriteFourthAmountTotal.text)
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black_000000)),
            span.length.minus(1),
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvWriteFourthAmountTotal.text = span
    }


    private fun initDetailForModify() {
        args.recruitDetailForModify?.let { originDetail ->
            with(originDetail) {
                writeViewModel.menuList.postValue(ArrayList(menu))
            }
        }
    }

    private fun observeUploadSuccess() {
        writeViewModel.writeSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                writeViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
                    navigateToUploadedPost(writePostId)
                }
            } else if (isSuccess.getContentIfNotHandled() == false) {
                LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.write_upload_fail_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeModifySuccess() {
        writeViewModel.writeModifySuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true)
                navigateToUploadedPost(args.recruitDetailForModify!!.recruitId ?: 0)
            else if (isSuccess.getContentIfNotHandled() == false) {
                LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.write_modify_fail_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToUploadedPost(recruitId: Int) {
        findNavController().navigateSafe(
            WriteFourthFragmentDirections.actionWriteFourthFragmentToPostFragment(
                recruitId
            )
        )
        writeViewModel.resetVariables()
    }
}