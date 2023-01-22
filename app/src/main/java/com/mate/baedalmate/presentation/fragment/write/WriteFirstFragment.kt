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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ListLiveData
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentWriteFirstBinding
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.domain.model.ShippingFeeDto
import com.mate.baedalmate.presentation.adapter.write.WriteFirstDeliveryFeeRangeAdapter
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DELIVERY_FEE_RANGE_MAX = 999999999

@AndroidEntryPoint
class WriteFirstFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFirstBinding>()
    private val args by navArgs<WriteFirstFragmentArgs>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var writeFirstDeliveryFeeRangeAdapter: WriteFirstDeliveryFeeRangeAdapter
    private val decimalFormat = DecimalFormat("#,###")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    private val currentDeliveryFeeList = mutableListOf<ShippingFeeDto>()
    private var deliveryFeeRangeCorrectList = ListLiveData<Boolean>()
    private var deliveryFeeRangeEmptyChkList = ListLiveData<Boolean>()

    private var chkDeadLineAmount = MutableLiveData(false)
    private var chkDeadLineTime = MutableLiveData(false)
    private var chkDeliveryFeeRange = MutableLiveData(true)
    private var chkDeliveryFee = MutableLiveData(true)
    private val onNext: MediatorLiveData<Boolean> = MediatorLiveData()
    private var onDeliveryFeeAdd: MediatorLiveData<Boolean> = MediatorLiveData()

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        initUserInputForms()
        initDetailForModify()
        setNextClickListener()
    }

    private fun setBackClickListener() {
        binding.btnWriteFirstActionbarBack.setOnDebounceClickListener {
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

    private fun initUserInputForms() {
        initDeadLinePeople()
        initGoalAmount()
        setGoalTimeInput()
        initDeadLineCriterion()
        initDeliveryFeeRange()
    }

    private fun initDetailForModify() {
        args.recruitDetailForModify?.let { originDetail ->
            with(originDetail) {
                initDetailForModifyDeadLinePeople(minPeople)
                initDetailForModifyGoalAmount(minPrice)
                initDetailForModifyGoalTime(deadlineDate)
                initDetailForModifyCriterion(criteria)
                initDetailForModifyDeliveryFeeRange(freeShipping, shippingFee)
            }
        }
    }

    private fun setNextClickListener() {
        with(binding.btnWriteFirstNext) {
            onNext.observe(viewLifecycleOwner) { isNextEnable ->
                this.isEnabled = isNextEnable
            }

            setOnDebounceClickListener {
                setUserInputInformation()
                findNavController().navigate(
                    WriteFirstFragmentDirections.actionWriteFirstFragmentToWriteSecondFragment(
                        recruitDetailForModify = args.recruitDetailForModify
                    )
                )
            }
        }
    }

    private fun initDeadLinePeople() {
        with(binding) {
            currentPeopleDeadLineCount = 1
            setDeadLinePeopleCountClickListener()
        }
    }

    private fun setDeadLinePeopleCountClickListener() {
        with(binding) {
            with(imgWriteFirstGoalPeopleDecrease) {
                isEnabled = (binding.currentPeopleDeadLineCount ?: 1) >= 2
                setOnDebounceClickListener(300L) {
                    binding.currentPeopleDeadLineCount =
                        (binding.currentPeopleDeadLineCount ?: 1) - 1
                    if ((binding.currentPeopleDeadLineCount ?: 1) <= 1) {
                        imgWriteFirstGoalPeopleDecrease.background =
                            Color.parseColor("#D9D9D9").toDrawable()
                        imgWriteFirstGoalPeopleDecrease.isEnabled = false
                    }
                }
                imgWriteFirstGoalPeopleIncrease.setOnDebounceClickListener(300L) {
                    binding.currentPeopleDeadLineCount =
                        (binding.currentPeopleDeadLineCount ?: 1) + 1
                    if ((binding.currentPeopleDeadLineCount ?: 1) >= 2) {
                        this.background =
                            ContextCompat.getDrawable(requireContext(), R.color.white_FFFFFF)
                        this.isEnabled = true
                    }
                }
            }
        }
    }

    private fun initGoalAmount() {
        validateDeadLineDeliveryInputForm()
        setGoalAmountFocusListener()
        setGoalAmountSoftKeyboardDoneClickListener()
    }

    private fun validateDeadLineDeliveryInputForm() {
        with(binding.etWriteFirstGoalDeliveryUserInput) {
            var currentEditTextInput = ""
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?, i1: Int, i2: Int, i3: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.let { currentText ->
                        with(currentText.toString()) {
                            setDeliveryFeeRangeError(isError = (this.isEmpty()))

                            if (!TextUtils.isEmpty(this) && this != currentEditTextInput) {
                                currentEditTextInput =
                                    decimalFormat.format(s.toString().replace(",", "").toDouble())
                                setText(currentEditTextInput)
                                setSelection(currentEditTextInput.length)

                                setDeliveryFeeRangeError(
                                    isError =
                                    currentEditTextInput.replace(",", "").toInt() <= 0
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    private fun setDeliveryFeeRangeError(isError: Boolean) {
        with(binding) {
            if (isError) {
                tvWriteFirstGoalDeliveryError.visibility = View.VISIBLE
                viewWriteFristGoalDeliveryUserInputBackground.background =
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.background_white_radius_10_stroke_red
                    )
                chkDeadLineAmount.postValue(false)
            } else {
                tvWriteFirstGoalDeliveryError.visibility = View.INVISIBLE
                viewWriteFristGoalDeliveryUserInputBackground.background =
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.background_white_radius_10
                    )
                chkDeadLineAmount.postValue(true)
            }
        }
    }

    private fun setGoalAmountFocusListener() {
        with(binding.etWriteFirstGoalDeliveryUserInput) {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.toString() == "0") {
                    setText("")
                } else if (!hasFocus && text.isEmpty()) {
                    setText("0")
                }
            }
        }
    }

    private fun setGoalAmountSoftKeyboardDoneClickListener() {
        with(binding.etWriteFirstGoalDeliveryUserInput) {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (text.isEmpty())
                            setText("0")
                        clearFocus()
                        false
                    }
                    else -> false
                }
            }
        }
    }

    private fun setGoalTimeInput() {
        with(binding) {
            etWriteFirstGoalTimePickerHour.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_writeFirstFragment_to_writeFirstGoalTimeDialogFragment)
            }
            etWriteFirstGoalTimePickerMinute.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_writeFirstFragment_to_writeFirstGoalTimeDialogFragment)
            }
            layoutWriteFirstGoalTimePicker.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_writeFirstFragment_to_writeFirstGoalTimeDialogFragment)
            }

            writeViewModel.deadLineTime?.observe(viewLifecycleOwner) { time ->
                val isTimeBlank = time != null
                chkDeadLineTime.postValue(isTimeBlank)

                if (isTimeBlank) {
                    val deadLineTime = LocalDateTime.parse(time, dateTimeFormatter)
                    etWriteFirstGoalTimePickerHour.setText("${deadLineTime.hour}")
                    etWriteFirstGoalTimePickerMinute.setText("${deadLineTime.minute}")
                }
            }
        }
    }

    private fun initDeadLineCriterion() {
        with(binding) {
            radiogroupWriteFirstGoalCriterion.setOnCheckedChangeListener { group, checkedId ->
                tvWriteFirstGoalCriterionDescription.text = when (checkedId) {
                    R.id.radiobutton_write_first_goal_criterion_people -> getString(R.string.write_first_goal_criterion_description_people)
                    R.id.radiobutton_write_first_goal_criterion_delivery -> getString(R.string.write_first_goal_criterion_description_delivery)
                    R.id.radiobutton_write_first_goal_criterion_time -> getString(R.string.write_first_goal_criterion_description_time)
                    else -> ""
                }
            }
        }
    }

    private fun initDeliveryFeeRange() {
        initDeliveryFeeRangeView()
        setCurrentDeliveryFeeRangeIsError()

        // 최초 작성시 WriteSecond로 넘어간뒤 다시 돌아왔을 때 초기 배달비 구간관련 chk 변수들이 false로 되어있어 다음으로 버튼이 갱신되지 않는 문제를 다시 체크하도록 처리하여 해결
        refreshOriginDeliveryFeeRangeCorrect()
    }

    private fun initDeliveryFeeRangeView() {
        initDeliveryFeeRangeAdapter()
        setDeliveryFeeRangeAddListener()
        setDeliveryFeeRangeRemoveListener()
    }

    private fun initDeliveryFeeRangeAdapter() {
        writeFirstDeliveryFeeRangeAdapter = WriteFirstDeliveryFeeRangeAdapter(
            currentDeliveryFeeList,
            deliveryFeeRangeCorrectList,
            deliveryFeeRangeEmptyChkList
        )

        with(binding.rvWriteFirstDeliveryFeeRange) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = writeFirstDeliveryFeeRangeAdapter
        }
    }

    private fun setDeliveryFeeRangeAddListener() {
        binding.btnWriteFirstDeliveryFeeRangeAdd.setOnDebounceClickListener {
            addDeliveryFeeRangeItem()
        }
    }

    private fun setDeliveryFeeRangeRemoveListener() {
        writeFirstDeliveryFeeRangeAdapter.setOnItemClickListener(object :
            WriteFirstDeliveryFeeRangeAdapter.OnItemClickListener {
            override fun removeItem(contents: ShippingFeeDto, pos: Int) {
                deleteDeliveryFeeRangeItem(pos)
            }
        })
    }

    private fun addDeliveryFeeRangeItem() {
        currentDeliveryFeeList.add(ShippingFeeDto(0, 0, DELIVERY_FEE_RANGE_MAX))
        deliveryFeeRangeCorrectList.add(true)
        deliveryFeeRangeEmptyChkList.add(false)

        with(writeFirstDeliveryFeeRangeAdapter) {
            notifyItemInserted(currentDeliveryFeeList.lastIndex)
            notifyItemRangeChanged(
                currentDeliveryFeeList.lastIndex - 1,
                currentDeliveryFeeList.size
            )
        }
        binding.scrollviewWriteFirst.smoothScrollToView(binding.btnWriteFirstDeliveryFeeRangeAdd)
    }

    private fun deleteDeliveryFeeRangeItem(pos: Int) {
        setDeleteCurrentRangeItemAnimation(pos)
        currentDeliveryFeeList.removeAt(pos)
        deliveryFeeRangeCorrectList.removeAt(pos)
        deliveryFeeRangeEmptyChkList.removeAt(pos)

        with(writeFirstDeliveryFeeRangeAdapter) {
            notifyItemRemoved(pos)
            notifyItemRangeChanged(0, currentDeliveryFeeList.size)
        }
    }

    private fun setDeleteCurrentRangeItemAnimation(pos: Int) {
        val deleteAnimation: Animation =
            AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_out_right)
        deleteAnimation.duration = 100
        binding.rvWriteFirstDeliveryFeeRange[pos].startAnimation(deleteAnimation)

    }

    private fun setCurrentDeliveryFeeRangeIsError() {
        setDeliveryFeeRangeAddEnable()
        validateDeliveryFeeRangeCorrect()
        displayDeliveryFeeRangeIsError()
    }

    private fun setDeliveryFeeRangeAddEnable() {
        onDeliveryFeeAdd.observe(viewLifecycleOwner) {
            binding.btnWriteFirstDeliveryFeeRangeAdd.isEnabled = it
        }
    }

    private fun validateDeliveryFeeRangeCorrect() {
        deliveryFeeRangeCorrectList.observe(viewLifecycleOwner) { list ->
            chkDeliveryFeeRange.postValue(!list.contains(false))
            binding.tvWriteFirstDeliveryFeeRangeError.visibility =
                if (list.contains(false)) View.VISIBLE else View.INVISIBLE
        }

        deliveryFeeRangeEmptyChkList.observe(viewLifecycleOwner) { list ->
            chkDeliveryFee.postValue(!list.contains(false))
        }
    }

    private fun displayDeliveryFeeRangeIsError() {
        with(binding) {
            radiogroupWriteFirstDeliveryFee.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radiobuttonWriteFirstDeliveryFeeFree.id -> {
                        chkDeliveryFee.postValue(true)
                        chkDeliveryFeeRange.postValue(true)
                        tvWriteFirstDeliveryFeeRangeError.visibility = View.INVISIBLE
                        btnWriteFirstDeliveryFeeRangeAdd.visibility = View.GONE
                        rvWriteFirstDeliveryFeeRange.visibility = View.GONE
                    }
                    radiobuttonWriteFirstDeliveryFeeFreeNot.id -> {
                        refreshOriginDeliveryFeeRangeCorrect()
                        btnWriteFirstDeliveryFeeRangeAdd.visibility = View.VISIBLE
                        rvWriteFirstDeliveryFeeRange.visibility = View.VISIBLE

                        if (currentDeliveryFeeList.isNullOrEmpty())
                            addDeliveryFeeRangeItem() // 첫번째 구간은 기본으로 추가될수 있도록 설정
                    }
                }
            }
        }
    }

    private fun refreshOriginDeliveryFeeRangeCorrect() {
        deliveryFeeRangeCorrectList.notifyChange()
        deliveryFeeRangeEmptyChkList.notifyChange()
    }

    private fun setUserInputInformation() {
        setUserInputInformationDeadLinePeopleCount()
        setUserInputInformationGoalAmount()
        setUserInputInformationCriterion()
        setUserInputInformationFeeRange()
    }

    private fun setUserInputInformationDeadLinePeopleCount() {
        writeViewModel.deadLinePeopleCount = binding.currentPeopleDeadLineCount ?: 1
    }

    private fun setUserInputInformationGoalAmount() {
        with(binding.etWriteFirstGoalDeliveryUserInput.text) {
            writeViewModel.deadLineAmount =
                if (this.isNotEmpty()) this.toString().replace(",", "").toInt()
                else 0
        }
    }

    private fun setUserInputInformationCriterion() {
        writeViewModel.deadLineCriterion =
            when (binding.radiogroupWriteFirstGoalCriterion.checkedRadioButtonId) {
                R.id.radiobutton_write_first_goal_criterion_people -> RecruitFinishCriteria.NUMBER
                R.id.radiobutton_write_first_goal_criterion_delivery -> RecruitFinishCriteria.PRICE
                R.id.radiobutton_write_first_goal_criterion_time -> RecruitFinishCriteria.TIME
                else -> RecruitFinishCriteria.NUMBER
            }
    }

    private fun setUserInputInformationFeeRange() {
        writeViewModel.isDeliveryFeeFree =
            binding.radiogroupWriteFirstDeliveryFee.checkedRadioButtonId == R.id.radiobutton_write_first_delivery_fee_free

        if (!writeViewModel.isDeliveryFeeFree) {
            val feeList = currentDeliveryFeeList
            feeList.sortWith(compareBy({ it.lowerPrice }, { it.upperPrice }))
            writeViewModel.deliveryFeeRangeList = feeList
        }
    }

    private fun initDetailForModifyDeadLinePeople(currentDeadLinePeople: Int) {
        with(currentDeadLinePeople) {
            binding.currentPeopleDeadLineCount = this
            binding.imgWriteFirstGoalPeopleDecrease.let { decreaseButton ->
                if (this > 1) {
                    decreaseButton.isEnabled = true
                    decreaseButton.background =
                        ContextCompat.getDrawable(requireContext(), R.color.white_FFFFFF)
                } else {
                    decreaseButton.isEnabled = false
                    decreaseButton.background =
                        Color.parseColor("#D9D9D9").toDrawable()
                }
            }
        }
    }

    private fun initDetailForModifyGoalAmount(currentMinPrice: Int) {
        with(binding) {
            etWriteFirstGoalDeliveryUserInput.setText(decimalFormat.format(currentMinPrice))
            tvWriteFirstGoalDeliveryError.visibility = View.INVISIBLE
            viewWriteFristGoalDeliveryUserInputBackground.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_white_radius_10
                )
            chkDeadLineAmount.postValue(true)
        }
    }

    private fun initDetailForModifyGoalTime(currentGoalTime: String) {
        writeViewModel.deadLineTime!!.postValue(currentGoalTime)
        val parsedDeadLineTime = LocalDateTime.parse(currentGoalTime, dateTimeFormatter)
        with(binding) {
            etWriteFirstGoalTimePickerHour.setText("${parsedDeadLineTime.hour}")
            etWriteFirstGoalTimePickerMinute.setText("${parsedDeadLineTime.minute}")
        }
        chkDeadLineTime.postValue(true)
    }

    private fun initDetailForModifyCriterion(currentCriteria: RecruitFinishCriteria) {
        when (currentCriteria) {
            RecruitFinishCriteria.NUMBER -> binding.radiogroupWriteFirstGoalCriterion.check(R.id.radiobutton_write_first_goal_criterion_people)
            RecruitFinishCriteria.PRICE -> binding.radiogroupWriteFirstGoalCriterion.check(R.id.radiobutton_write_first_goal_criterion_delivery)
            RecruitFinishCriteria.TIME -> binding.radiogroupWriteFirstGoalCriterion.check(R.id.radiobutton_write_first_goal_criterion_time)
        }
    }

    private fun initDetailForModifyDeliveryFeeRange(
        isDeliveryFeeFree: Boolean,
        newDeliveryFeeRanges: List<ShippingFeeDto>
    ) {
        if (isDeliveryFeeFree) {
            binding.radiogroupWriteFirstDeliveryFee.check(R.id.radiobutton_write_first_delivery_fee_free)
        } else {
            binding.radiogroupWriteFirstDeliveryFee.check(R.id.radiobutton_write_first_delivery_fee_free_not)
            currentDeliveryFeeList.let { originFeeRanges ->
                // 배달비 구간 무료배송 아님 체크시 자동으로 구간이 추가되어있는 것을 비우고 전부 새로 추가
                if (originFeeRanges.isNotEmpty()) {
                    originFeeRanges.clear()
                    deliveryFeeRangeCorrectList.clear(true)
                    deliveryFeeRangeEmptyChkList.clear(true)
                }

                originFeeRanges.addAll(newDeliveryFeeRanges)
                deliveryFeeRangeCorrectList.addAll(List(newDeliveryFeeRanges.size) { true })
                deliveryFeeRangeEmptyChkList.addAll(List(newDeliveryFeeRanges.size) { true })

                with(writeFirstDeliveryFeeRangeAdapter) {
                    notifyItemRangeInserted(0, originFeeRanges.size)
                    notifyItemRangeChanged(0, originFeeRanges.size)
                }
            }
        }
    }

    fun NestedScrollView.smoothScrollToView(
        view: View,
        marginTop: Int = 0,
        maxDuration: Long = 300L,
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