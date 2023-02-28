package com.mate.baedalmate.presentation.fragment.post

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ListLiveData
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.LoadingAlertDialog
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentPostMenuBottomSheetDialogBinding
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.presentation.adapter.write.WriteFourthMenuListAdapter
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class PostMenuBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentPostMenuBottomSheetDialogBinding>()
    private val args by navArgs<PostMenuBottomSheetDialogFragmentArgs>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()
    private lateinit var writeFourthMenuListAdapter: WriteFourthMenuListAdapter
    private lateinit var loadingAlertDialog: AlertDialog
    private var addedMenuList = ListLiveData<MenuDto>()
    private var dishCount = 1
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var chkSubjectIsNotEmpty = MutableLiveData(false)
    private var chkAmountIsNotEmpty = MutableLiveData(true)
    private val onAdd: MediatorLiveData<Boolean> = MediatorLiveData()
    init {
        onAdd.addSource(chkSubjectIsNotEmpty) {
            onAdd.value = _onAdd()
        }
        onAdd.addSource(chkAmountIsNotEmpty) {
            onAdd.value = _onAdd()
        }
    }

    private fun _onAdd(): Boolean {
        if ((chkSubjectIsNotEmpty.value == true) and (chkAmountIsNotEmpty.value == true)) {
            return true
        }
        return false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return initBottomSheetDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostMenuBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboardTouchOtherArea(view)
        initAlertDialog()
        initMenuNameEditText()
        validateDeadLineDeliveryInputForm()
        setAddMenuClickListener()
        initDishCountListener()
        setParticipateClickListener()
        observeParticipateSuccess()
        setMenuListOriginalValue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun initBottomSheetDialog(): BottomSheetDialog {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRadius)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.behavior.skipCollapsed = true // Dialog가 길어지는 경우 Half_expand되는 경우 방지
        bottomSheetDialog.behavior.isHideable = false
        return bottomSheetDialog
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboardTouchOtherArea(view: View) {
        var startClickTime = 0L;
        with(binding) {
            rvPostMenuAddedList.setOnTouchListener { view, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis()
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    setHideKeyboard(startClickTime)
                }
                false
            }
            layoutScrollPostMenuAdded.setOnTouchListener { view, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis()
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    setHideKeyboard(startClickTime)
                }
                false
            }
            scrollviewPostMenuBottomSheetDialog.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis()
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    setHideKeyboard(startClickTime)
                }
                false
            }
        }
    }

    private fun setHideKeyboard(startClickTime: Long) {
        if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
            val inputMethodManager: InputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etPostMenuSubjectInput.windowToken, 0)
            inputMethodManager.hideSoftInputFromWindow(binding.etPostMenuAmountInput.windowToken, 0)
        }
    }

    private fun initAlertDialog() {
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        loadingAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initMenuNameEditText() {
        binding.etPostMenuSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                chkSubjectIsNotEmpty.postValue(s.toString().trim().isNotBlank())
            }
        })
    }

    private fun validateDeadLineDeliveryInputForm() {
        with(binding.etPostMenuAmountInput) {
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
                    chkAmountIsNotEmpty.postValue(s.toString().isNotBlank())
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
        onAdd.observe(viewLifecycleOwner) { isAddEnable ->
            binding.tvPostMenuAdd.isEnabled = isAddEnable
        }

        with(binding) {
            tvPostMenuAdd.setOnDebounceClickListener {
                addedMenuList.add(
                    MenuDto(
                        name = etPostMenuSubjectInput.text.toString(),
                        price = etPostMenuAmountInput.text.toString().replace(",", "").toInt(),
                        quantity = currentDishCount.toString().replace(",", "").toInt()
                    )
                )
                etPostMenuSubjectInput.setText("")
                etPostMenuAmountInput.setText("")
                currentDishCount = 1
                dishCount = 1
            }
        }
    }

    private fun initDishCountListener() {
        with(binding) {
            currentDishCount = dishCount

            with(imgPostMenuDishCountDecrease) {
                this.isEnabled = false // 초기 false 설정

                binding.imgPostMenuDishCountIncrease.setOnDebounceClickListener(300L) {
                    dishCount++
                    binding.currentDishCount = dishCount
                    if (dishCount >= 2) {
                        this.background =
                            ContextCompat.getDrawable(requireContext(), R.color.white_FFFFFF)
                        this.strokeColor = ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.gray_line_EBEBEB
                        )
                        this.strokeWidth = 1.dp.toFloat()
                        this.isEnabled = true
                    }
                }

                this.setOnDebounceClickListener(300L) {
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

    private fun setParticipateClickListener() {
        binding.btnPostFrontContentsParticipate.setOnDebounceClickListener {
            showLoadingDialog()
            recruitViewModel.requestParticipateRecruitPost(
                menuList = addedMenuList.value?.toList() ?: listOf(),
                roomId = args.recruitId
            )
        }
    }

    private fun observeParticipateSuccess() {
        recruitViewModel.recruitPostParticipateInfo.observe(viewLifecycleOwner) { info ->
            findNavController().navigate(
                PostMenuBottomSheetDialogFragmentDirections.actionPostMenuBottomSheetDialogFragmentToChatFragment(
                    roomId = info.chatRoomId
                )
            )
        }
    }

    private fun setMenuListOriginalValue() {
        addedMenuList.observe(viewLifecycleOwner) {
            TransitionManager.beginDelayedTransition(
                binding.root as ViewGroup,
                AutoTransition().apply { duration = 150L })
            setMenuListAdapter(menuList = it)

            if (!it.isNullOrEmpty()) {
                writeFourthMenuListAdapter.notifyDataSetChanged()
                binding.layoutPostMenuAdded.visibility = View.VISIBLE
                binding.btnPostFrontContentsParticipate.isEnabled = true

                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED // HALF EXPAND를 false 처리했음에도 처음에 동적으로 크기가 변하는 경우 절반만 보여지는 상태에 의해 해결을 위해 추가
            } else {
                binding.layoutPostMenuAdded.visibility = View.GONE
                binding.btnPostFrontContentsParticipate.isEnabled = false
            }
            setMenuDeleteClickListener()
//            setMenuTotalAmount(menuList = it)
        }
    }

    private fun setMenuListAdapter(menuList: MutableList<MenuDto>) {
        writeFourthMenuListAdapter = WriteFourthMenuListAdapter(menuList)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false).apply {
                reverseLayout = true
                stackFromEnd = true
            }
        with(binding.rvPostMenuAddedList) {
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

    private fun showLoadingDialog() {
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog)
    }
}