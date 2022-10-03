package com.mate.baedalmate.presentation.fragment.write

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitFinishCriteria
import com.mate.baedalmate.data.datasource.remote.recruit.ShippingFeeDto
import com.mate.baedalmate.databinding.FragmentWriteFirstBinding
import com.mate.baedalmate.databinding.ItemWriteFirstDeliveryFeeRangeBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs
import java.text.DecimalFormat

@AndroidEntryPoint
class WriteFirstFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFirstBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private var leastPeopleCount = 1
    private var deliveryFeeRangeCorrectList = mutableListOf<Boolean>(true)
    private var deliveryFeeRangeEmptyChkList = mutableListOf<Boolean>(false)

    private var chkDeadLineAmount = MutableLiveData(false)
    private var chkDeliveryFeeRange = MutableLiveData(true)
    private var chkDeliveryFee = MutableLiveData(true)
    private val onNext: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        onNext.addSource(chkDeadLineAmount) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeliveryFeeRange) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeliveryFee) {
            onNext.value = _onNext()
        }
    }

    private fun _onNext(): Boolean {
        if ((chkDeadLineAmount.value == true) and (chkDeliveryFee.value == true) and (chkDeliveryFeeRange.value == true)) {
            return true
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setNextClickListener()
        initDeadLinePeople()
        validateDeadLineDeliveryInputForm()
        initDeadLineCriterion()
        initDeliveryFeeRange()
        setDeliveryFeeRangeAddClickListener()
    }

    private fun setBackClickListener() {
        binding.btnWriteFirstActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        onNext.observe(viewLifecycleOwner) {
            binding.btnWriteFirstNext.isEnabled = it
        }

        binding.btnWriteFirstNext.setOnClickListener {
            writeViewModel.deadLinePeopleCount = binding.currentPeopleDeadLineCount ?: 0
            if (binding.etWriteFirstGoalDeliveryUserInput.text.isNotEmpty()) {
                writeViewModel.deadLineAmount =
                    binding.etWriteFirstGoalDeliveryUserInput.text.toString().replace(",", "")
                        .toInt()
            } else {
                writeViewModel.deadLineAmount = 0
            }
            when (binding.radiogroupWriteFirstGoalCriterion.checkedRadioButtonId) {
                R.id.radiobutton_write_first_goal_criterion_people -> {
                    writeViewModel.deadLineCriterion = RecruitFinishCriteria.NUMBER
                }
                R.id.radiobutton_write_first_goal_criterion_delivery -> {
                    writeViewModel.deadLineCriterion = RecruitFinishCriteria.PRICE
                }
                R.id.radiobutton_write_first_goal_criterion_time -> {
                    writeViewModel.deadLineCriterion = RecruitFinishCriteria.TIME
                }
            }
            if (binding.radiogroupWriteFirstDeliveryFee.checkedRadioButtonId == R.id.radiobutton_write_first_delivery_fee_free) {
                writeViewModel.isDeliveryFeeFree = true
            } else {
                writeViewModel.isDeliveryFeeFree = false
                val feeList = mutableListOf<ShippingFeeDto>()
                for (i in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
                    val view = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(i)
                    val startRange =
                        view.findViewById<EditText>(R.id.et_delivery_fee_range_start).text.toString()
                            .replace(",", "").toInt()
                    val endRange =
                        view.findViewById<EditText>(R.id.et_delivery_fee_range_end).text.toString()
                            .replace(",", "").toInt()
                    val deliveryFee =
                        view.findViewById<EditText>(R.id.et_delivery_fee).text.toString()
                            .replace(",", "").toInt()
                    feeList.add(
                        ShippingFeeDto(
                            lowerPrice = startRange,
                            upperPrice = endRange,
                            shippingFee = deliveryFee
                        )
                    )
                }
                feeList.sortWith(compareBy({ it.lowerPrice }, { it.upperPrice }))
                writeViewModel.deliveryFeeRangeList = feeList
            }
        }
    }

    private fun initDeadLinePeople() {
        with(binding) {
            currentPeopleDeadLineCount = leastPeopleCount
            with(imgWriteFirstGoalPeopleDecrease) {
                isEnabled = false
                this.setOnClickListener {
                    leastPeopleCount--
                    binding.currentPeopleDeadLineCount = leastPeopleCount
                    if (leastPeopleCount <= 1) {
                        this.background = Color.parseColor("#D9D9D9").toDrawable()
                        this.isEnabled = false
                    }
                }

                imgWriteFirstGoalPeopleIncrease.setOnClickListener {
                    leastPeopleCount++
                    currentPeopleDeadLineCount = leastPeopleCount
                    if (leastPeopleCount >= 2) {
                        this.background =
                            ContextCompat.getDrawable(requireContext(), R.color.white_FFFFFF)
                        this.isEnabled = true
                    }
                }
            }
        }
    }

    private fun validateDeadLineDeliveryInputForm() {
        with(binding.etWriteFirstGoalDeliveryUserInput) {
            this.setText("0")
            var result = ""
            val decimalFormat = DecimalFormat("#,###")
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}
                override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                        result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                        this@with.setText(result)
                        this@with.setSelection(result.length)

                        if (result.replace(",", "").toInt() > 0) {
                            binding.tvWriteFirstGoalDeliveryError.visibility = View.INVISIBLE
                            binding.viewWriteFristGoalDeliveryUserInputBackground.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10
                                )
                            chkDeadLineAmount.postValue(true)
                        } else {
                            binding.tvWriteFirstGoalDeliveryError.visibility = View.VISIBLE
                            binding.viewWriteFristGoalDeliveryUserInputBackground.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                            chkDeadLineAmount.postValue(false)
                        }
                    } else if (s.isEmpty()) {
                        binding.tvWriteFirstGoalDeliveryError.visibility = View.VISIBLE
                        binding.viewWriteFristGoalDeliveryUserInputBackground.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                        chkDeadLineAmount.postValue(false)
                    }
                }
            })

            this@with.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && this@with.text.toString() == "0") {
                    this@with.setText("")
                } else if (!hasFocus && this@with.text.isEmpty()) {
                    this@with.setText("0")
                }
            }

            this@with.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (this@with.text.isEmpty())
                            this@with.setText("0")
                        this@with.clearFocus()
                        false
                    }
                    else -> false
                }
            }
        }
    }

    private fun initDeliveryFeeRange() {
        with(binding) {
            radiogroupWriteFirstDeliveryFee.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radiobuttonWriteFirstDeliveryFeeFree.id -> {
                        chkDeliveryFee.postValue(true)
                        chkDeliveryFeeRange.postValue(true)
                        tvWriteFirstDeliveryFeeRangeError.visibility = View.INVISIBLE
                        btnWriteFirstDeliveryFeeRangeAdd.visibility = View.GONE
                        layoutWriteFirstDeliveryFeeRange.visibility = View.GONE
                    }
                    radiobuttonWriteFirstDeliveryFeeFreeNot.id -> {
                        validateDeliveryFeeRangeCorrect()
                        btnWriteFirstDeliveryFeeRangeAdd.visibility = View.VISIBLE
                        layoutWriteFirstDeliveryFeeRange.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setDeliveryFeeRangeAddClickListener() {
        var result = ""
        val decimalFormat = DecimalFormat("#,###")

        binding.btnWriteFirstDeliveryFeeRangeAdd.setOnClickListener {
            val deliveryFeeRangeViewBinding =
                ItemWriteFirstDeliveryFeeRangeBinding.inflate(LayoutInflater.from(context))
            val currentIdx = binding.layoutWriteFirstDeliveryFeeRange.childCount
            val startRangeTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                        result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                        deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.setText(result)
                        deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.setSelection(result.length)
                    }

                    deliveryFeeRangeEmptyChkList[currentIdx] = !(deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFee.text.isNullOrEmpty())

                    if (s.toString().isNotEmpty() &&
                        deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.text.isNotEmpty()) {
                        if (deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.text.toString()
                                .replace(",", "")
                                .toInt() >
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.text.toString()
                                .replace(",", "")
                                .toInt()
                        ) {
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                            deliveryFeeRangeCorrectList[currentIdx] = false
                        } else {
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10
                                )
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10
                                )
                            deliveryFeeRangeCorrectList[currentIdx] = true
                        }
                    }
                    validateDeliveryFeeRangeCorrect()
                }
            }
            val endRangeTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                        result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                        deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.setText(result)
                        deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.setSelection(result.length)
                    }

                    deliveryFeeRangeEmptyChkList[currentIdx] =
                        !(deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFee.text.isNullOrEmpty())

                    if (s.toString()
                            .isNotEmpty() && deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.text.isNotEmpty()
                    ) {
                        if (deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.text.toString()
                                .replace(",", "")
                                .toInt() >
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.text.toString()
                                .replace(",", "")
                                .toInt()
                        ) {
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                            deliveryFeeRangeCorrectList[currentIdx] = false
                        } else {
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10
                                )
                            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10
                                )
                            deliveryFeeRangeCorrectList[currentIdx] = true
                        }
                    }
                    validateDeliveryFeeRangeCorrect()
                }
            }
            val deliveryFeeTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                        result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                        deliveryFeeRangeViewBinding.etDeliveryFee.setText(result)
                        deliveryFeeRangeViewBinding.etDeliveryFee.setSelection(result.length)
                    }

                    deliveryFeeRangeEmptyChkList[currentIdx] =
                        !(deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFee.text.isNullOrEmpty())
                    validateDeliveryFeeRangeCorrect()
                }
            }

            deliveryFeeRangeViewBinding.btnDeliveryFeeRangeDelete.setOnClickListener {
                binding.layoutWriteFirstDeliveryFeeRange.removeViewAt(currentIdx)
                deliveryFeeRangeCorrectList.removeAt(currentIdx)
                deliveryFeeRangeEmptyChkList.removeAt(currentIdx)
                for (i in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
                    val view = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(i)
                    view.findViewById<EditText>(R.id.et_delivery_fee_range_start)
                        .removeTextChangedListener(startRangeTextWatcher)
                    view.findViewById<EditText>(R.id.et_delivery_fee_range_end)
                        .removeTextChangedListener(endRangeTextWatcher)
                    view.findViewById<EditText>(R.id.et_delivery_fee)
                        .removeTextChangedListener(deliveryFeeTextWatcher)
                }
                setDeliveryFeeRangeDelete()
                setDeliveryFeeRangeError()
                validateDeliveryFeeRangeCorrect()
            }

            deliveryFeeRangeViewBinding.etDeliveryFeeRangeStart.addTextChangedListener(
                startRangeTextWatcher
            )
            deliveryFeeRangeViewBinding.etDeliveryFeeRangeEnd.addTextChangedListener(
                endRangeTextWatcher
            )
            deliveryFeeRangeViewBinding.etDeliveryFee.addTextChangedListener(
                deliveryFeeTextWatcher
            )

            deliveryFeeRangeViewBinding.tvDeliveryFeeRangeTitle.text = "${getString(R.string.section)} ${currentIdx.plus(1)}"

            val deliveryFeeRangeView = deliveryFeeRangeViewBinding.root
            binding.layoutWriteFirstDeliveryFeeRange.addView(deliveryFeeRangeView)
            deliveryFeeRangeCorrectList.add(true)
            deliveryFeeRangeEmptyChkList.add(false)
            binding.scrollviewWriteFirst.smoothScrollToView(binding.btnWriteFirstDeliveryFeeRangeAdd)
            validateDeliveryFeeRangeCorrect()
        }
        setDeliveryFeeRangeFirstCheck()
    }

    private fun setDeliveryFeeRangeDelete() {
        for (i in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
            val view = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(i)
            val deleteButton = view.findViewById<ImageButton>(R.id.btn_delivery_fee_range_delete)
            val rangeTitle = view.findViewById<TextView>(R.id.tv_delivery_fee_range_title)
            rangeTitle.text = "${getString(R.string.section)} ${i.plus(1)}"
            deleteButton.setOnClickListener {
                binding.layoutWriteFirstDeliveryFeeRange.removeViewAt(i)
                deliveryFeeRangeCorrectList.removeAt(i)
                deliveryFeeRangeEmptyChkList.removeAt(i)
                setDeliveryFeeRangeDelete()
                setDeliveryFeeRangeError()
                validateDeliveryFeeRangeCorrect()
            }
        }
    }

    private fun setDeliveryFeeRangeError() {
        for (position in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
            val view = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(position)
            val startEdit = view.findViewById<EditText>(R.id.et_delivery_fee_range_start)
            val endEdit = view.findViewById<EditText>(R.id.et_delivery_fee_range_end)
            val feeEdit = view.findViewById<EditText>(R.id.et_delivery_fee)

            startEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    deliveryFeeRangeEmptyChkList[position] = !(startEdit.text.isEmpty() || endEdit.text.isEmpty() || feeEdit.text.isEmpty())
                    if (s.toString().isNotEmpty() && endEdit.text.isNotEmpty()) {
                        if (startEdit.text.toString().replace(",", "")
                                .toInt() > endEdit.text.toString().replace(",", "").toInt()
                        ) {
                            startEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                            endEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                            deliveryFeeRangeCorrectList[position] = false
                        } else {
                            startEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                            endEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                            deliveryFeeRangeCorrectList[position] = true
                        }
                    }
                    validateDeliveryFeeRangeCorrect()
                }
            })

            endEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    deliveryFeeRangeEmptyChkList[position] =
                        !(startEdit.text.isEmpty() || endEdit.text.isEmpty() || feeEdit.text.isEmpty())
                    if (startEdit.text.isNotEmpty() && endEdit.text.isNotEmpty()) {
                        if (startEdit.text.toString().replace(",", "")
                                .toInt() > endEdit.text.toString().replace(",", "").toInt()
                        ) {
                            startEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                            endEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                            deliveryFeeRangeCorrectList[position] = false
                        } else {
                            startEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                            endEdit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                            deliveryFeeRangeCorrectList[position] = true
                        }
                    }
                    validateDeliveryFeeRangeCorrect()
                }
            })
            feeEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    deliveryFeeRangeEmptyChkList[position] =
                        !(startEdit.text.isEmpty() || endEdit.text.isEmpty() || feeEdit.text.isEmpty())
                    validateDeliveryFeeRangeCorrect()
                }
            })
        }
    }

    private fun setDeliveryFeeRangeFirstCheck() {
        var result = ""
        val decimalFormat = DecimalFormat("#,###")

        binding.etDeliveryFeeRangeStart.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                    result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                    binding.etDeliveryFeeRangeStart.setText(result)
                    binding.etDeliveryFeeRangeStart.setSelection(result.length)
                }

                deliveryFeeRangeEmptyChkList[0] = !(binding.etDeliveryFeeRangeStart.text.isEmpty() || binding.etDeliveryFeeRangeEnd.text.isEmpty() || binding.etDeliveryFee.text.isEmpty())

                if (s.toString().isNotEmpty() && binding.etDeliveryFeeRangeEnd.text.isNotEmpty()) {
                    if (binding.etDeliveryFeeRangeStart.text.toString().replace(",", "").toInt() >
                        binding.etDeliveryFeeRangeEnd.text.toString().replace(",", "").toInt()
                    ) {
                        binding.etDeliveryFeeRangeStart.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                        binding.etDeliveryFeeRangeEnd.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                        deliveryFeeRangeCorrectList[0] = false
                    } else {
                        binding.etDeliveryFeeRangeStart.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                        binding.etDeliveryFeeRangeEnd.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                        deliveryFeeRangeCorrectList[0] = true
                    }
                }
                validateDeliveryFeeRangeCorrect()
            }
        })
        binding.etDeliveryFeeRangeEnd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                    result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                    binding.etDeliveryFeeRangeEnd.setText(result)
                    binding.etDeliveryFeeRangeEnd.setSelection(result.length)
                }
                deliveryFeeRangeEmptyChkList[0] =
                    !(binding.etDeliveryFeeRangeStart.text.isEmpty() || binding.etDeliveryFeeRangeEnd.text.isEmpty() || binding.etDeliveryFee.text.isEmpty())

                if (s.toString()
                        .isNotEmpty() && binding.etDeliveryFeeRangeStart.text.isNotEmpty()
                ) {
                    if (binding.etDeliveryFeeRangeStart.text.toString().replace(",", "").toInt() >
                        binding.etDeliveryFeeRangeEnd.text.toString().replace(",", "").toInt()
                    ) {
                        binding.etDeliveryFeeRangeStart.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                        binding.etDeliveryFeeRangeEnd.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                        deliveryFeeRangeCorrectList[0] = false
                    } else {
                        binding.etDeliveryFeeRangeStart.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                        binding.etDeliveryFeeRangeEnd.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10
                            )
                        deliveryFeeRangeCorrectList[0] = true
                    }
                }
                validateDeliveryFeeRangeCorrect()
            }
        })

        binding.etDeliveryFee.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                    result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                    binding.etDeliveryFee.setText(result)
                    binding.etDeliveryFee.setSelection(result.length)
                }
                deliveryFeeRangeEmptyChkList[0] =
                    !(binding.etDeliveryFeeRangeStart.text.isEmpty() || binding.etDeliveryFeeRangeEnd.text.isEmpty() || binding.etDeliveryFee.text.isEmpty())
                validateDeliveryFeeRangeCorrect()
            }
        })
    }

    private fun validateDeliveryFeeRangeCorrect() {
        if (deliveryFeeRangeCorrectList.contains(false)) {
            binding.tvWriteFirstDeliveryFeeRangeError.visibility = View.VISIBLE
            chkDeliveryFeeRange.postValue(false)
        } else {
            binding.tvWriteFirstDeliveryFeeRangeError.visibility = View.INVISIBLE
            chkDeliveryFeeRange.postValue(true)
        }

        if (deliveryFeeRangeEmptyChkList.contains(false)) {
            chkDeliveryFee.postValue(false)
        } else {
            chkDeliveryFee.postValue(true)
        }
    }

    fun NestedScrollView.smoothScrollToView(
        view: View,
        marginTop: Int = 0,
        maxDuration: Long = 100L,
        onEnd: () -> Unit = {}
    ) {
        if (this.getChildAt(0).height <= this.height) { // 스크롤의 의미가 없다.
            onEnd()
            return
        }
        val y = computeDistanceToView(view) - marginTop
        val ratio = abs(y - this.scrollY) / (this.getChildAt(0).height - this.height).toFloat()
        ObjectAnimator.ofInt(this, "scrollY", y).apply {
            duration = (maxDuration * ratio).toLong()
            interpolator = AccelerateDecelerateInterpolator()
            doOnEnd {
                onEnd()
            }
            start()
        }
    }

    private fun initDeadLineCriterion() {
        binding.radiogroupWriteFirstGoalCriterion.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiobutton_write_first_goal_criterion_people -> {
                    binding.tvWriteFirstGoalCriterionDescription.text =
                        getString(R.string.write_first_goal_criterion_description_people)
                }
                R.id.radiobutton_write_first_goal_criterion_delivery -> {
                    binding.tvWriteFirstGoalCriterionDescription.text =
                        getString(R.string.write_first_goal_criterion_description_delivery)
                }
                R.id.radiobutton_write_first_goal_criterion_time -> {
                    binding.tvWriteFirstGoalCriterionDescription.text =
                        getString(R.string.write_first_goal_criterion_description_time)
                }
            }
        }
    }

    private fun NestedScrollView.computeDistanceToView(view: View): Int {
        return abs(calculateRectOnScreen(this).top - (this.scrollY + calculateRectOnScreen(view).top))
    }

    private fun calculateRectOnScreen(view: View): Rect {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Rect(
            location[0],
            location[1],
            location[0] + view.measuredWidth,
            location[1] + view.measuredHeight
        )
    }
}