package com.mate.baedalmate.presentation.fragment.post

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ListLiveData
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
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
    private var addedMenuList = ListLiveData<MenuDto>()
    private var dishCount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostMenuBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenuNameEditText()
        validateDeadLineDeliveryInputForm()
        setAddMenuClickListener()
        initDishCountListener()
        setParticipateClickListener()
        observeParticipateSuccess()
        setMenuListOriginalValue()
    }

    private fun initMenuNameEditText() {
        binding.etPostMenuSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tvPostMenuAdd.isEnabled = s.toString().trim().isNotBlank()
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
            tvPostMenuAdd.setOnClickListener {
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
                binding.imgPostMenuDishCountIncrease.setOnClickListener {
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

    private fun setParticipateClickListener() {
        binding.btnPostFrontContentsParticipate.setOnClickListener {
            recruitViewModel.requestParticipateRecruitPost(
                menuList = addedMenuList.value?.toList() ?: listOf(),
                roomId = args.roomId
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
            setMenuListAdapter(menuList = it)

            if (!it.isNullOrEmpty()) {
                writeFourthMenuListAdapter.notifyDataSetChanged()
                binding.layoutPostMenuAdded.visibility = View.VISIBLE
                binding.btnPostFrontContentsParticipate.isEnabled = true
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
}