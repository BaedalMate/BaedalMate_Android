package com.mate.baedalmate.presentation.fragment.chat

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ListLiveData
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.databinding.FragmentChangeOrderBinding
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.presentation.adapter.write.WriteFourthMenuListAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class ChangeOrderFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentChangeOrderBinding>()
    private val args by navArgs<ChangeOrderFragmentArgs>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private lateinit var writeFourthMenuListAdapter: WriteFourthMenuListAdapter
    private var addedMenuList = ListLiveData<MenuDto>()
    private var dishCount = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRadius)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMyMenuList()
        observeMyMenuList()
        initMenuNameEditText()
        validateDeadLineDeliveryInputForm()
        setAddMenuClickListener()
        initDishCountListener()
        setChangeOrderClickListener()
        observeChangeOrderSuccess()
        setMenuListOriginalValue()
    }

    private fun getMyMenuList() {
        chatViewModel.getMyMenuList(args.recruitId)
    }

    private fun observeMyMenuList() {
        chatViewModel.myMenuList.observe(viewLifecycleOwner) { myMenu ->
            addedMenuList.clear(false)
            val myMenuList = myMenu.menu
            addedMenuList.addAll(myMenuList)
        }
    }

    private fun initMenuNameEditText() {
        binding.etChangeOrderSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tvChangeOrderAdd.isEnabled = s.toString().trim().isNotBlank()
            }
        })
    }

    private fun validateDeadLineDeliveryInputForm() {
        with(binding.etChangeOrderAmountInput) {
            var result = ""
            val decimalFormat = DecimalFormat("#,###")

            this@with.setText("0")
            this@with.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    i1: Int,
                    i2: Int,
                    i3: Int
                ) {
                }

                override fun onTextChanged(
                    charSequence: CharSequence?,
                    i1: Int,
                    i2: Int,
                    i3: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                        result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                        this@with.setText(result)
                        this@with.setSelection(result.length)
                    }
                }
            })

            this@with.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && this@with.text.toString() == "0") {
                    this@with.setText("")
                }
            }

            this@with.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (this@with.text.isEmpty()) this@with.setText("0")
                        this@with.clearFocus()
                        false
                    }
                    else -> false
                }
            }
        }
    }

    private fun setAddMenuClickListener() {
        with(binding) {
            tvChangeOrderAdd.setOnClickListener {
                addedMenuList.add(
                    MenuDto(
                        name = etChangeOrderSubjectInput.text.toString(),
                        price = etChangeOrderAmountInput.text.toString().replace(",", "").toInt(),
                        quantity = currentDishCount.toString().replace(",", "").toInt()
                    )
                )
                etChangeOrderSubjectInput.setText("")
                etChangeOrderAmountInput.setText("")
                currentDishCount = 1
                dishCount = 1
            }
        }
    }

    private fun initDishCountListener() {
        with(binding) {
            currentDishCount = dishCount

            with(imgChangeOrderDishCountDecrease) {
                binding.imgChangeOrderDishCountIncrease.setOnClickListener {
                    dishCount++
                    binding.currentDishCount = dishCount
                    if (dishCount >= 2) {
                        this.background =
                            androidx.core.content.ContextCompat.getDrawable(
                                requireContext(),
                                com.mate.baedalmate.R.color.white_FFFFFF
                            )
                        this.strokeColor = androidx.core.content.ContextCompat.getColorStateList(
                            requireContext(),
                            com.mate.baedalmate.R.color.gray_line_EBEBEB
                        )
                        this.strokeWidth = 1.dp.toFloat()
                        this.isEnabled = true
                    }
                }

                this.setOnClickListener {
                    dishCount--
                    binding.currentDishCount = dishCount
                    if (dishCount <= 1) {
                        this.background = android.graphics.Color.parseColor("#D9D9D9").toDrawable()
                        this.strokeColor = null
                        this.strokeWidth = 0f
                        this.isEnabled = false
                    }
                }
            }
        }
    }

    private fun setChangeOrderClickListener() {
        binding.btnChangeOrderSubmit.setOnClickListener {
            chatViewModel.putChangeMyMenuList(
                changedMenuList = addedMenuList.value?.toList() ?: listOf(),
                recruitId = args.recruitId
            )
        }
    }

    private fun observeChangeOrderSuccess() {
        chatViewModel.isChangeMyMenuListSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.change_order_success_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.change_order_fail_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setMenuListOriginalValue() {
        addedMenuList.observe(viewLifecycleOwner) {
            setMenuListAdapter(menuList = it)

            if (!it.isNullOrEmpty()) {
                writeFourthMenuListAdapter.notifyDataSetChanged()
                binding.layoutChangeOrderAdded.visibility = View.VISIBLE
                binding.btnChangeOrderSubmit.isEnabled = true
            } else {
                binding.layoutChangeOrderAdded.visibility = View.GONE
                binding.btnChangeOrderSubmit.isEnabled = false
            }
            setMenuDeleteClickListener()
        }
    }

    private fun setMenuListAdapter(menuList: MutableList<MenuDto>) {
        writeFourthMenuListAdapter = WriteFourthMenuListAdapter(menuList)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        with(binding.rvChangeOrderAddedList) {
            this.layoutManager = layoutManager
            this.adapter = writeFourthMenuListAdapter
        }
    }

    private fun setMenuDeleteClickListener() {
        writeFourthMenuListAdapter.setOnItemClickListener(object :
            WriteFourthMenuListAdapter.OnItemClickListener {
            override fun deleteMenu(contents: MenuDto, pos: Int) {
                addedMenuList.removeAt(pos)
                writeFourthMenuListAdapter.notifyDataSetChanged()
            }
        })
    }
}