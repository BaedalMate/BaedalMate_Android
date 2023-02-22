package com.mate.baedalmate.presentation.fragment.write

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.databinding.FragmentWriteSecondBinding
import com.mate.baedalmate.domain.model.DeliveryPlatform
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.presentation.adapter.write.WriteSecondDormitorySpinnerAdapter
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat


@AndroidEntryPoint
class WriteSecondFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteSecondBinding>()
    private val args by navArgs<WriteSecondFragmentArgs>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var spinnerAdapter: WriteSecondDormitorySpinnerAdapter

    private var chkStoreDescription = MutableLiveData(false)
    private var chkCoupon = MutableLiveData(true)
    private val onNext: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        onNext.addSource(chkStoreDescription) {
            onNext.value = _onNext()
        }
        onNext.addSource(chkCoupon) {
            onNext.value = _onNext()
        }
    }

    private fun _onNext(): Boolean {
        if ((chkStoreDescription.value == true) and (chkCoupon.value == true)) {
            return true
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setNextClickListener()
        initDormitorySpinner()
        initStoreLocation()
        initPlatform()
        setPlatformClickListener()
        setCouponInputForm()
        validateCouponInputForm()
        initDetailForModify()
    }

    private fun setBackClickListener() {
        binding.btnWriteSecondActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        onNext.observe(viewLifecycleOwner) {
            binding.btnWriteSecondNext.isEnabled = it
        }

        binding.btnWriteSecondNext.setOnClickListener {
            val selectedDormitory =
                when (binding.spinnerWriteSecondUserLocationInputDormitory.selectedItem) {
                    "누리학사" -> Dormitory.NURI
                    "성림학사" -> Dormitory.SUNGLIM
                    "KB학사" -> Dormitory.KB
                    "불암학사" -> Dormitory.BURAM
                    "수림학사" -> Dormitory.SULIM
                    else -> Dormitory.NURI
                }
            writeViewModel.deliveryDormitory = selectedDormitory
            writeViewModel.deliveryPlatform =
                when (binding.radiogroupWriteSecondPlatformList.checkedRadioButtonId) {
                    R.id.radiobutton_write_second_platform_baemin -> DeliveryPlatform.BAEMIN
                    R.id.radiobutton_write_second_platform_yogiyo -> DeliveryPlatform.YOGIYO
                    R.id.radiobutton_write_second_platform_coupang -> DeliveryPlatform.COUPANG
                    R.id.radiobutton_write_second_platform_ddangyo -> DeliveryPlatform.DDGNGYO
                    R.id.radiobutton_write_second_platform_etc -> DeliveryPlatform.ETC
                    else -> DeliveryPlatform.ETC
                }

            if (binding.checkboxWriteSecondCouponUse.isChecked) {
                writeViewModel.isCouponUse = false
            } else {
                writeViewModel.isCouponUse = true
                writeViewModel.couponAmount =
                    binding.etWriteSecondCouponUserInput.text.toString().replace(",", "").toInt()
            }
            findNavController().navigate(
                WriteSecondFragmentDirections.actionWriteSecondFragmentToWriteThirdFragment(
                    args.recruitDetailForModify
                )
            )
        }
    }

    private fun initDormitorySpinner() {
        val items = resources.getStringArray(R.array.dormitory_list)
        spinnerAdapter = WriteSecondDormitorySpinnerAdapter(
            requireContext(),
            R.layout.item_spinner_dormitory_list,
            items.toMutableList()
        )
        binding.spinnerWriteSecondUserLocationInputDormitory.adapter = spinnerAdapter
    }

    private fun initStoreLocation() {
        binding.imgWriteSecondStoreLocationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_writeSecondFragment_to_writeSecondPlaceDialogFragment)
        }
        binding.tvWriteSecondStoreLocation.setOnClickListener {
            findNavController().navigate(R.id.action_writeSecondFragment_to_writeSecondPlaceDialogFragment)
        }

        writeViewModel.deliveryStore.observe(viewLifecycleOwner) { placeDto ->
            binding.tvWriteSecondStoreLocation.text = placeDto.name
            if (binding.tvWriteSecondStoreLocation.text.isNotEmpty()) {
                chkStoreDescription.postValue(true)
            } else {
                chkStoreDescription.postValue(false)
            }
        }
    }

    private fun initPlatform() {
        val deviceWidth = GetDeviceSize.getDeviceWidthSizeDp(requireContext())
        val useWidth = deviceWidth - 52.dp // 4번의 12dp margin을 만들었음에 따라 52dp를 빼서 계산

        with(binding.radiogroupWriteSecondPlatformList) {
            for (i in 0 until this.childCount) {
                val radioButton = this.getChildAt(i) as RadioButton
                radioButton.updateLayoutParams<RadioGroup.LayoutParams> {
                    width = useWidth
                    height = useWidth
                }
            }

            displayCurrentCheckedPlatform(this, checkedRadioButtonId)
        }
    }

    private fun setPlatformClickListener() {
        binding.radiogroupWriteSecondPlatformList.setOnCheckedChangeListener { radioGroup, checkedId ->
            displayCurrentCheckedPlatform(radioGroup, checkedId)
        }
    }

    private fun displayCurrentCheckedPlatform(
        currentRadioGroup: RadioGroup,
        currentCheckedRadioButtonId: Int
    ) {
        for (radioButton in currentRadioGroup.children) {
            if (radioButton.id == currentCheckedRadioButtonId) {
                (radioButton as RadioButton).background.colorFilter = null
            } else {
                val currentBackground = (radioButton as RadioButton).background
                (radioButton as RadioButton).background = convertToGrayscale(currentBackground)
            }
        }
    }

    private fun setCouponInputForm() {
        binding.checkboxWriteSecondCouponUse.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etWriteSecondCouponUserInput.isEnabled = false
                binding.viewWriteSecondCouponUserInputBackground.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10_stroke_gray_line
                    )
                binding.tvWriteSecondCouponUserInputUnit.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_line_EBEBEB
                    )
                )
                binding.etWriteSecondCouponUserInput.setText("0")
                binding.etWriteSecondCouponUserInput.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_line_EBEBEB
                    )
                )
                binding.tvWriteSecondCouponInputError.visibility = View.INVISIBLE
                chkCoupon.postValue(true)
            } else {
                binding.etWriteSecondCouponUserInput.isEnabled = true
                binding.viewWriteSecondCouponUserInputBackground.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10
                    )
                binding.tvWriteSecondCouponUserInputUnit.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_000000
                    )
                )
                binding.etWriteSecondCouponUserInput.setText("0")
                binding.etWriteSecondCouponUserInput.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_000000
                    )
                )
                chkCoupon.postValue(false)
            }
        }
    }

    private fun validateCouponInputForm() {
        with(binding.etWriteSecondCouponUserInput) {
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
                            binding.tvWriteSecondCouponInputError.visibility = View.INVISIBLE
                            binding.viewWriteSecondCouponUserInputBackground.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_white_radius_10
                                )
                            chkCoupon.postValue(true)
                        } else {
                            if (!binding.checkboxWriteSecondCouponUse.isChecked) {
                                binding.tvWriteSecondCouponInputError.visibility = View.VISIBLE
                                binding.viewWriteSecondCouponUserInputBackground.background =
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.background_white_radius_10_stroke_red
                                    )
                                chkCoupon.postValue(false)
                            }
                        }
                    } else if (s.isEmpty() && !binding.checkboxWriteSecondCouponUse.isChecked) {
                        binding.tvWriteSecondCouponInputError.visibility = View.VISIBLE
                        binding.viewWriteSecondCouponUserInputBackground.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.background_white_radius_10_stroke_red
                            )
                        chkCoupon.postValue(false)
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

    private fun initDetailForModify() {
        args.recruitDetailForModify?.let { originDetail ->
            with(originDetail) {
                initDetailForModifyDormitory(dormitory)
                initDetailForModifyStore(place)
                initDetailForModifyPlatform(platform)
                initDetailForModifyCoupon(coupon)
            }
        }
    }

    private fun initDetailForModifyDormitory(dormitory: String) {
        val originDormitory =
            when (dormitory) {
                Dormitory.NURI.name -> "누리학사"
                Dormitory.SUNGLIM.name -> "성림학사"
                Dormitory.KB.name -> "KB학사"
                Dormitory.BURAM.name -> "불암학사"
                Dormitory.SULIM.name -> "수림학사"
                else -> "누리학사"
            }

        binding.spinnerWriteSecondUserLocationInputDormitory.setSelection(
            resources.getStringArray(R.array.dormitory_list).indexOf(originDormitory)
        )
    }

    private fun initDetailForModifyStore(storeLocation: PlaceDto) {
        writeViewModel.deliveryStore.postValue(storeLocation)
    }

    private fun initDetailForModifyPlatform(platform: DeliveryPlatform) {
        when (platform) {
            DeliveryPlatform.BAEMIN -> binding.radiogroupWriteSecondPlatformList.check(R.id.radiobutton_write_second_platform_baemin)
            DeliveryPlatform.YOGIYO -> binding.radiogroupWriteSecondPlatformList.check(R.id.radiobutton_write_second_platform_yogiyo)
            DeliveryPlatform.COUPANG -> binding.radiogroupWriteSecondPlatformList.check(R.id.radiobutton_write_second_platform_coupang)
            DeliveryPlatform.DDGNGYO -> binding.radiogroupWriteSecondPlatformList.check(R.id.radiobutton_write_second_platform_ddangyo)
            DeliveryPlatform.ETC -> binding.radiogroupWriteSecondPlatformList.check(R.id.radiobutton_write_second_platform_etc)
        }
    }

    private fun initDetailForModifyCoupon(couponPrice: Int) {
        binding.checkboxWriteSecondCouponUse.isChecked = couponPrice == 0
        if (couponPrice != 0) {
            binding.etWriteSecondCouponUserInput.setText("$couponPrice")
        }
    }

    private fun convertToGrayscale(drawable: Drawable): Drawable? {
        val matrix = ColorMatrix()
        matrix.setSaturation(0F)
        val filter = ColorMatrixColorFilter(matrix)
        drawable.colorFilter = filter
        return drawable
    }
}