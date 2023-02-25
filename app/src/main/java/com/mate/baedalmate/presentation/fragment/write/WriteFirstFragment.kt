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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.HideKeyBoardUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentWriteFirstBinding
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WriteFirstFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFirstBinding>()
    private val args by navArgs<WriteFirstFragmentArgs>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val decimalFormat = DecimalFormat("#,###")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    private var chkDeadLineAmount = MutableLiveData(false)
    private var chkDeadLineTime = MutableLiveData(false)
    private var chkDeliveryFee = MutableLiveData(true)
    private val onNext: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        onNext.addSource(chkDeadLineAmount) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeadLineTime) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkDeliveryFee) {
            onNext.value = _onNext()
        }
    }

    private fun _onNext(): Boolean {
        if ((chkDeadLineAmount.value == true) and (chkDeadLineTime.value == true) and (chkDeliveryFee.value == true)) {
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
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), binding.scrollviewWriteFirst)
        setBackClickListener()
        initUserInputForms()
        initDetailForModify()
        setNextClickListener()
    }

    override fun onResume() {
        super.onResume()

        // 최초 작성시 WriteSecond로 넘어간뒤 다시 돌아왔을 때 초기 배달비 구간관련 chk 변수들이 false로 되어있어 다음으로 버튼이 갱신되지 않는 문제를 다시 체크하도록 처리하여 해결
        refreshOriginDeliveryFeeCorrect()
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
        initDeliveryFee()
    }

    private fun initDetailForModify() {
        args.recruitDetailForModify?.let { originDetail ->
            with(originDetail) {
                initDetailForModifyDeadLinePeople(minPeople)
                initDetailForModifyGoalAmount(minPrice)
                initDetailForModifyGoalTime(deadlineDate)
                initDetailForModifyCriterion(criteria)
                initDetailForModifyDeliveryFee(freeShipping, shippingFee)
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
            currentPeopleDeadLineCount = MIN_PEOPLE_COUNT
            setDeadLinePeopleCountClickListener()
        }
    }

    private fun setDeadLinePeopleCountClickListener() {
        with(binding) {
            with(imgWriteFirstGoalPeopleDecrease) {
                isEnabled = (binding.currentPeopleDeadLineCount ?: MIN_PEOPLE_COUNT) >= 3
                setOnDebounceClickListener(300L) {
                    binding.currentPeopleDeadLineCount =
                        (binding.currentPeopleDeadLineCount ?: MIN_PEOPLE_COUNT) - 1
                    if ((binding.currentPeopleDeadLineCount ?: MIN_PEOPLE_COUNT) <= MIN_PEOPLE_COUNT) {
                        imgWriteFirstGoalPeopleDecrease.background =
                            Color.parseColor("#D9D9D9").toDrawable()
                        imgWriteFirstGoalPeopleDecrease.isEnabled = false
                    }
                }
                imgWriteFirstGoalPeopleIncrease.setOnDebounceClickListener(300L) {
                    binding.currentPeopleDeadLineCount =
                        (binding.currentPeopleDeadLineCount ?: MIN_PEOPLE_COUNT) + 1
                    if ((binding.currentPeopleDeadLineCount ?: MIN_PEOPLE_COUNT) >= 3) {
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
                ) {}

                override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                    if (charSequence != null) checkDeliveryGoalAmountIsEmpty(charSequence)
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.let { currentText ->
                        with(currentText.toString()) {
                            checkDeliveryGoalAmountIsEmpty(s)
                            if (!TextUtils.isEmpty(this) && this != currentEditTextInput) {
                                currentEditTextInput =
                                    decimalFormat.format(s.toString().replace(",", "").toDouble())
                                setText(currentEditTextInput)
                                setSelection(currentEditTextInput.length)

                                checkDeliveryGoalAmountIsEmpty(s)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun checkDeliveryGoalAmountIsEmpty(currentString: CharSequence) {
        with(binding) {
            if (currentString.isNullOrEmpty() || currentString.toString().replace(",", "")
                    .toInt() <= 0
            ) {
                tvWriteFirstGoalDeliveryError.visibility = View.VISIBLE
                viewWriteFristGoalDeliveryUserInputBackground.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10_stroke_red
                    )
                chkDeadLineAmount.postValue(false)
            } else {
                tvWriteFirstGoalDeliveryError.visibility = View.INVISIBLE
                viewWriteFristGoalDeliveryUserInputBackground.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10
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
                with(tvWriteFirstGoalCriterionDescription) {
                    // 변경됐음을 보여주기 위해 깜빡거림및 슬라이딩 효과를 주기위해 일시적으로 GONE/VISIBLE 처리
                    visibility = View.GONE
                    visibility = View.VISIBLE

                    text = when (checkedId) {
                        R.id.radiobutton_write_first_goal_criterion_people -> getString(R.string.write_first_goal_criterion_description_people)
                        R.id.radiobutton_write_first_goal_criterion_delivery -> getString(R.string.write_first_goal_criterion_description_delivery)
                        R.id.radiobutton_write_first_goal_criterion_time -> getString(R.string.write_first_goal_criterion_description_time)
                        else -> ""
                    }
                }
            }
        }
    }

    private fun initDeliveryFee() {
        validateDeliveryFeeInputForm()
        observeDeliveryFeeIsFree()
        setDeliveryFeeFocusListener()
        setDeliveryFeeSoftKeyboardDoneClickListener()
    }

    private fun validateDeliveryFeeInputForm() {
        var currentEditTextInput = ""
        binding.etWriteFirstDeliveryFeeUserInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkDeliveryFeeError(false)
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { currentText ->
                    with(currentText.toString()) {
                        if (!TextUtils.isEmpty(this) && this != currentEditTextInput) {
                            currentEditTextInput = decimalFormat.format(
                                currentText.toString().replace(",", "").toDouble()
                            )
                            binding.etWriteFirstDeliveryFeeUserInput.setText(currentEditTextInput)
                            binding.etWriteFirstDeliveryFeeUserInput.setSelection(
                                currentEditTextInput.length
                            )

                            checkDeliveryFeeError(false)
                        }
                    }
                }
            }
        })
    }

    private fun observeDeliveryFeeIsFree() {
        with(binding) {
            radiogroupWriteFirstDeliveryFee.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radiobuttonWriteFirstDeliveryFeeFree.id -> {
                        checkDeliveryFeeError(true)
                    }
                    radiobuttonWriteFirstDeliveryFeeFreeNot.id -> {
                        checkDeliveryFeeError(false)
                    }
                }
            }
        }
    }

    private fun refreshOriginDeliveryFeeCorrect() {
        checkDeliveryFeeError(writeViewModel.isDeliveryFeeFree)
    }

    private fun checkDeliveryFeeError(isDeliveryFeeFree: Boolean) {
        with(binding) {
            if (isDeliveryFeeFree) {
                HideKeyBoardUtil.hideEditText(requireContext(), binding.etWriteFirstDeliveryFeeUserInput)
                layoutWriteFirstDeliveryFeeUserInput.visibility = View.GONE
                chkDeliveryFee.postValue(true)
                tvWriteFirstDeliveryFeeError.visibility = View.INVISIBLE
            } else {
                layoutWriteFirstDeliveryFeeUserInput.visibility = View.VISIBLE
                checkDeliveryFeeIsNotEmpty(binding.etWriteFirstDeliveryFeeUserInput.text)
            }
        }
    }

    private fun checkDeliveryFeeIsNotEmpty(currentString: CharSequence) {
        if (currentString.isNullOrEmpty() || currentString.toString().replace(",", "")
                .toInt() <= 0
        ) {
            chkDeliveryFee.postValue(false)
            binding.viewWriteFirstDeliveryFeeUserInputBackground.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_white_radius_10_stroke_red
                )
            binding.tvWriteFirstDeliveryFeeError.visibility = View.VISIBLE
        } else {
            chkDeliveryFee.postValue(true)
            binding.viewWriteFirstDeliveryFeeUserInputBackground.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)
            binding.tvWriteFirstDeliveryFeeError.visibility = View.INVISIBLE
        }
    }

    private fun setDeliveryFeeFocusListener() {
        with(binding.etWriteFirstDeliveryFeeUserInput) {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.toString() == "0") {
                    setText("")
                } else if (!hasFocus && text.isEmpty()) {
                    setText("0")
                }
            }
        }
    }

    private fun setDeliveryFeeSoftKeyboardDoneClickListener() {
        with(binding.etWriteFirstDeliveryFeeUserInput) {
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

    private fun setUserInputInformation() {
        setUserInputInformationDeadLinePeopleCount()
        setUserInputInformationGoalAmount()
        setUserInputInformationCriterion()
        setUserInputInformationFee()
    }

    private fun setUserInputInformationDeadLinePeopleCount() {
        writeViewModel.deadLinePeopleCount = binding.currentPeopleDeadLineCount ?: MIN_PEOPLE_COUNT
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

    private fun setUserInputInformationFee() {
        writeViewModel.isDeliveryFeeFree =
            binding.radiogroupWriteFirstDeliveryFee.checkedRadioButtonId == R.id.radiobutton_write_first_delivery_fee_free

        if (!writeViewModel.isDeliveryFeeFree) {
            writeViewModel.deliveryFee =
                with(binding.etWriteFirstDeliveryFeeUserInput.text) {
                    if (this.isNullOrEmpty()) 0
                    else this.toString().replace(",", "").toInt()
                }
        }
    }

    private fun initDetailForModifyDeadLinePeople(currentDeadLinePeople: Int) {
        with(currentDeadLinePeople) {
            binding.currentPeopleDeadLineCount = this
            binding.imgWriteFirstGoalPeopleDecrease.let { decreaseButton ->
                if (this > MIN_PEOPLE_COUNT) {
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

    private fun initDetailForModifyDeliveryFee(
        isDeliveryFeeFree: Boolean,
        newDeliveryFee: Int
    ) {
        with(binding) {
            if (isDeliveryFeeFree) radiogroupWriteFirstDeliveryFee.check(R.id.radiobutton_write_first_delivery_fee_free)
            else {
                radiogroupWriteFirstDeliveryFee.check(R.id.radiobutton_write_first_delivery_fee_free_not)
                etWriteFirstDeliveryFeeUserInput.setText(decimalFormat.format(newDeliveryFee))
            }
        }
    }

    companion object {
        const val MIN_PEOPLE_COUNT = 2
    }
}