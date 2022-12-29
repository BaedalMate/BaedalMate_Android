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
import androidx.activity.OnBackPressedCallback
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
import com.mate.baedalmate.common.ExtendedEditText
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.domain.model.ShippingFeeDto
import com.mate.baedalmate.databinding.FragmentWriteFirstBinding
import com.mate.baedalmate.databinding.ItemWriteFirstDeliveryFeeRangeBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WriteFirstFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFirstBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private var leastPeopleCount = 1
    private var deliveryFeeRangeCorrectList = mutableListOf<Boolean>(true)
    private var deliveryFeeRangeEmptyChkList = mutableListOf<Boolean>(false)

    private var chkDeadLineAmount = MutableLiveData(false)
    private var chkDeadLineTime = MutableLiveData(false)
    private var chkDeliveryFeeRange = MutableLiveData(true)
    private var chkDeliveryFee = MutableLiveData(true)
    private val onNext: MediatorLiveData<Boolean> = MediatorLiveData()
    private var onDeliveryFeeAdd : MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        onNext.addSource(chkDeadLineAmount) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeadLineTime) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeliveryFeeRange) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeliveryFee) {
            onNext.value = _onNext()
        }
        onDeliveryFeeAdd.addSource(chkDeliveryFeeRange) {
            onDeliveryFeeAdd.value = _onDeliveryFeeAdd()
        }
        onDeliveryFeeAdd.addSource(chkDeliveryFee) {
            onDeliveryFeeAdd.value = _onDeliveryFeeAdd()
        }
    }

    private fun _onNext(): Boolean {
        if ((chkDeadLineAmount.value == true) and (chkDeadLineTime.value == true) and (chkDeliveryFee.value == true) and (chkDeliveryFeeRange.value == true)) {
            return true
        }
        return false
    }

    private fun _onDeliveryFeeAdd(): Boolean {
        if ((chkDeliveryFee.value == true) and (chkDeliveryFeeRange.value == true)) {
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
        setDeliveryFeeRangeAddEnable()
        setDeliveryFeeRangeAddClickListener()
        setGoalTimeInput()
    }

    private fun setBackClickListener() {
        binding.btnWriteFirstActionbarBack.setOnClickListener {
            findNavController().navigateUp()
            writeViewModel.resetVariables()
        }

        val resetVariableOnBackPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                writeViewModel.resetVariables()
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            resetVariableOnBackPressed
        )
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
                        view.findViewById<ExtendedEditText>(R.id.et_delivery_fee_range).text.toString()
                            .replace(",", "").toInt()
                    val deliveryFee =
                        view.findViewById<ExtendedEditText>(R.id.et_delivery_fee).text.toString()
                            .replace(",", "").toInt()
                    feeList.add(
                        ShippingFeeDto(
                            lowerPrice = startRange,
                            upperPrice = 999999999,
                            shippingFee = deliveryFee
                        )
                    )
                }
                feeList.sortWith(compareBy({ it.lowerPrice }, { it.upperPrice }))
                writeViewModel.deliveryFeeRangeList = feeList
            }
            findNavController().navigate(R.id.action_writeFirstFragment_to_writeSecondFragment)
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

    private fun setDeliveryFeeRangeAddEnable() {
        onDeliveryFeeAdd.observe(viewLifecycleOwner) {
            binding.btnWriteFirstDeliveryFeeRangeAdd.isEnabled = it
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
                        deliveryFeeRangeViewBinding.etDeliveryFeeRange.setText(result)
                        deliveryFeeRangeViewBinding.etDeliveryFeeRange.setSelection(result.length)
                    }

                    deliveryFeeRangeEmptyChkList[currentIdx] =
                        !(deliveryFeeRangeViewBinding.etDeliveryFeeRange.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFee.text.isNullOrEmpty())

                    if (s.toString().isNotEmpty()) {
                        if (deliveryFeeRangeViewBinding.etDeliveryFeeRange.text.toString()
                                .replace(",", "")
                                .toInt() <=
                            // 직전의 range
                            binding.layoutWriteFirstDeliveryFeeRange.getChildAt(currentIdx - 1)
                                .findViewById<EditText>(R.id.et_delivery_fee_range).text.toString()
                                .replace(",", "")
                                .toInt()
                        ) {
                            deliveryFeeRangeViewBinding.etDeliveryFeeRange.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                            deliveryFeeRangeCorrectList[currentIdx] = false
                        } else {
                            deliveryFeeRangeViewBinding.etDeliveryFeeRange.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.selector_edittext_white_gray_line_radius_10
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
                        !(deliveryFeeRangeViewBinding.etDeliveryFeeRange.text.isNullOrEmpty() ||
                                deliveryFeeRangeViewBinding.etDeliveryFee.text.isNullOrEmpty())
                    validateDeliveryFeeRangeCorrect()
                }
            }

            deliveryFeeRangeViewBinding.btnDeliveryFeeRangeDelete.setOnClickListener {
                setDeliveryFeeRangeDeleteClickListener(currentIdx)
                setDeliveryFeeRangeDelete()
                setDeliveryFeeRangeError()
                validateDeliveryFeeRangeCorrect()
            }

            deliveryFeeRangeViewBinding.etDeliveryFeeRange.addTextChangedListener(
                startRangeTextWatcher
            )
            deliveryFeeRangeViewBinding.etDeliveryFee.addTextChangedListener(
                deliveryFeeTextWatcher
            )

            deliveryFeeRangeViewBinding.tvDeliveryFeeRangeTitle.text =
                "${getString(R.string.section)} ${currentIdx.plus(1)}"

            val deliveryFeeRangeView = deliveryFeeRangeViewBinding.root
            binding.layoutWriteFirstDeliveryFeeRange.addView(deliveryFeeRangeView)
            deliveryFeeRangeCorrectList.add(true)
            deliveryFeeRangeEmptyChkList.add(false)
            binding.scrollviewWriteFirst.smoothScrollToView(binding.btnWriteFirstDeliveryFeeRangeAdd)

            setPreviousFeeRangeDisable()
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
                setDeliveryFeeRangeDeleteClickListener(i)
            }
        }
        setPreviousFeeRangeDisable()
    }

    private fun setDeliveryFeeRangeDeleteClickListener(index: Int) {
        for (i in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
            val view = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(i)
            view.findViewById<ExtendedEditText>(R.id.et_delivery_fee_range).clearTextChangedListeners()
            view.findViewById<ExtendedEditText>(R.id.et_delivery_fee).clearTextChangedListeners()
        }

        binding.layoutWriteFirstDeliveryFeeRange.removeViewAt(index)
        deliveryFeeRangeCorrectList.removeAt(index)
        deliveryFeeRangeEmptyChkList.removeAt(index)
        setDeliveryFeeRangeDelete()
        setDeliveryFeeRangeError()
        validateDeliveryFeeRangeCorrect()
    }

    private fun setDeliveryFeeRangeError() {
        for (position in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
            var result = ""
            val decimalFormat = DecimalFormat("#,###")

            val view = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(position)
            val startEdit = view.findViewById<ExtendedEditText>(R.id.et_delivery_fee_range)
            val feeEdit = view.findViewById<ExtendedEditText>(R.id.et_delivery_fee)

            startEdit.addTextChangedListener(object : TextWatcher {
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
                        startEdit.setText(result)
                        startEdit.setSelection(result.length)
                    }

                    deliveryFeeRangeEmptyChkList[position] =
                        !(startEdit.text.isEmpty() || feeEdit.text.isEmpty())
                    if(position != 0) {
                        if (s.toString().isNotEmpty()) {
                            if (startEdit.text.toString().replace(",", "")
                                    .toInt() <= binding.layoutWriteFirstDeliveryFeeRange.getChildAt(position-1).findViewById<ExtendedEditText>(R.id.et_delivery_fee_range).text.toString().replace(",", "").toInt()
                            ) {
                                startEdit.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                                deliveryFeeRangeCorrectList[position] = false
                            } else {
                                startEdit.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.selector_edittext_white_gray_line_radius_10
                                )
                                deliveryFeeRangeCorrectList[position] = true
                            }
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
                    if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                        result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                        feeEdit.setText(result)
                        feeEdit.setSelection(result.length)
                    }
                    deliveryFeeRangeEmptyChkList[position] =
                        !(startEdit.text.isEmpty() || feeEdit.text.isEmpty())
                    validateDeliveryFeeRangeCorrect()
                }
            })
        }
    }

    private fun setDeliveryFeeRangeFirstCheck() {
        var result = ""
        val decimalFormat = DecimalFormat("#,###")

        binding.etDeliveryFeeRange.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s!!.toString()) && s.toString() != result) {
                    result = decimalFormat.format(s.toString().replace(",", "").toDouble())
                    binding.etDeliveryFeeRange.setText(result)
                    binding.etDeliveryFeeRange.setSelection(result.length)
                }

                deliveryFeeRangeEmptyChkList[0] =
                    !(binding.etDeliveryFeeRange.text.isEmpty() || binding.etDeliveryFee.text.isEmpty())
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
                    !(binding.etDeliveryFeeRange.text.isEmpty() || binding.etDeliveryFee.text.isEmpty())
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

    private fun setPreviousFeeRangeDisable() {
        for (idx in 0 until binding.layoutWriteFirstDeliveryFeeRange.childCount) {
            val currentView = binding.layoutWriteFirstDeliveryFeeRange.getChildAt(idx)
            if(idx == binding.layoutWriteFirstDeliveryFeeRange.childCount - 1) {
                currentView.findViewById<ExtendedEditText>(R.id.et_delivery_fee_range).isEnabled = true
                currentView.findViewById<ExtendedEditText>(R.id.et_delivery_fee).isEnabled = true
            } else {
                currentView.findViewById<ExtendedEditText>(R.id.et_delivery_fee_range).isEnabled = false
                currentView.findViewById<ExtendedEditText>(R.id.et_delivery_fee).isEnabled = false
            }
        }
    }

    private fun setGoalTimeInput() {
        binding.etWriteFirstGoalTimePickerHour.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_writeFirstFragment_to_writeFirstGoalTimeDialogFragment)
        }
        binding.etWriteFirstGoalTimePickerMinute.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_writeFirstFragment_to_writeFirstGoalTimeDialogFragment)
        }
        binding.layoutWriteFirstGoalTimePicker.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_writeFirstFragment_to_writeFirstGoalTimeDialogFragment)
        }

        writeViewModel.deadLineTime?.observe(viewLifecycleOwner) { time ->
            if (time != null) {
                val deadLineTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                binding.etWriteFirstGoalTimePickerHour.setText("${deadLineTime.hour}")
                binding.etWriteFirstGoalTimePickerMinute.setText("${deadLineTime.minute}")
                chkDeadLineTime.postValue(true)
            } else chkDeadLineTime.postValue(false)
        }
    }
}