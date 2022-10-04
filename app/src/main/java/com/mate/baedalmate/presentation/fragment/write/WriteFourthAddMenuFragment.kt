package com.mate.baedalmate.presentation.fragment.write

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.databinding.FragmentWriteFourthAddMenuBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class WriteFourthAddMenuFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentWriteFourthAddMenuBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private var dishCount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFourthAddMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenuNameEditText()
        validateDeadLineDeliveryInputForm()
        setAddMenuClickListener()
        initDishCountListener()
    }

    private fun initMenuNameEditText() {
        binding.etWriteFourthAddMenuSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tvWriteFourthAddMenu.isEnabled = s.toString().trim().isNotBlank()
            }
        })
    }

    private fun validateDeadLineDeliveryInputForm() {
        with(binding.etWriteFourthAddMenuAmountInput) {
            var result = ""
            val decimalFormat = DecimalFormat("#,###")

            this@with.setText("0")
            this@with.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}
                override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}
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
            tvWriteFourthAddMenu.setOnClickListener {
                writeViewModel.menuList.add(
                    MenuDto(
                        name = etWriteFourthAddMenuSubjectInput.text.toString(),
                        price = etWriteFourthAddMenuAmountInput.text.toString().replace(",", "")
                            .toInt(),
                        quantity = currentDishCount.toString().replace(",", "").toInt()
                    )
                )
                findNavController().navigateUp()
            }
        }
    }

    private fun initDishCountListener() {
        with(binding) {
            currentDishCount = dishCount

            with(imgWriteFourthDishCountDecrease) {
                binding.imgWriteFourthDishCountIncrease.setOnClickListener {
                    dishCount++
                    binding.currentDishCount = dishCount
                    if (dishCount >= 2) {
                        this.background = ContextCompat.getDrawable(requireContext(), R.color.white_FFFFFF)
                        this.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.gray_line_EBEBEB)
                        this.strokeWidth = 1.dp.toFloat()
                        this.isEnabled = true
                    }
                }

                this.setOnClickListener {
                    dishCount--
                    binding.currentDishCount = dishCount
                    if (dishCount <= 1) {
                        this.background = Color.parseColor("#D9D9D9").toDrawable()
                        this.strokeColor = null
                        this.strokeWidth = 0f
                        this.isEnabled = false
                    }
                }
            }
        }
    }
}