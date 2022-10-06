package com.mate.baedalmate.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentHomeChangeDormitoryBinding
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeChangeDormitoryFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentHomeChangeDormitoryBinding>()
    private val args by navArgs<HomeChangeDormitoryFragmentArgs>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeChangeDormitoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadioGroup()
        setConfirmClickListener()
    }

    private fun initRadioGroup() {
        when (args.currentDormitory) {
            getString(R.string.sunglim) -> {
                changeSelectedRadioButton(
                    binding.radiobuttonHomeChangeDormitorySunglim,
                    R.id.radiobutton_home_change_dormitory_sunglim
                )
            }
            getString(R.string.kb) -> {
                changeSelectedRadioButton(
                    binding.radiobuttonHomeChangeDormitoryKb,
                    R.id.radiobutton_home_change_dormitory_kb
                )
            }
            getString(R.string.buram) -> {
                changeSelectedRadioButton(
                    binding.radiobuttonHomeChangeDormitoryBuram,
                    R.id.radiobutton_home_change_dormitory_buram
                )
            }
            getString(R.string.nuri) -> {
                changeSelectedRadioButton(
                    binding.radiobuttonHomeChangeDormitoryNuri,
                    R.id.radiobutton_home_change_dormitory_nuri
                )
            }
            getString(R.string.sulim) -> {
                changeSelectedRadioButton(
                    binding.radiobuttonHomeChangeDormitorySulim,
                    R.id.radiobutton_home_change_dormitory_sulim
                )
            }
        }

        binding.radiogroupHomeChangeDormitory.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until binding.radiogroupHomeChangeDormitory.childCount) {
                if (binding.radiogroupHomeChangeDormitory.getChildAt(i) is RadioButton) {
                    val radioButton =
                        binding.radiogroupHomeChangeDormitory.getChildAt(i) as RadioButton
                    if (radioButton.id == checkedId) {
                        radioButton.setTextAppearance(R.style.style_semi_title_kor)
                        radioButton.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.main_FB5F1C
                            )
                        )
                        radioButton.compoundDrawableTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.main_FB5F1C)
                    } else {
                        radioButton.setTextAppearance(R.style.style_body2_kor)
                        radioButton.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.gray_dark_666666
                            )
                        )
                        radioButton.compoundDrawableTintList = null
                    }
                }
            }
        }
    }

    private fun changeSelectedRadioButton(radioButton: RadioButton, radioButtonId: Int) {
        with(binding) {
            radiogroupHomeChangeDormitory.check(radioButtonId)
            with(radioButton) {
                setTextAppearance(R.style.style_semi_title_kor)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C))
                compoundDrawableTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.main_FB5F1C)
            }
        }
    }

    private fun setConfirmClickListener() {
        binding.tvHomeChangeDormitoryConfirm.setOnClickListener {
            val newDormitoryId = binding.radiogroupHomeChangeDormitory.checkedRadioButtonId
            val selectedRadioButton = binding.root.findViewById<RadioButton>(newDormitoryId)
            val newDormitory = when (selectedRadioButton.text) {
                getString(R.string.sunglim) -> Dormitory.SUNGLIM
                getString(R.string.kb) -> Dormitory.KB
                getString(R.string.buram) -> Dormitory.BURAM
                getString(R.string.nuri) -> Dormitory.NURI
                getString(R.string.sulim) -> Dormitory.SULIM
                else -> Dormitory.SUNGLIM
            }
            memberViewModel.requestChangeUserDormitory(newDormitory = newDormitory)
        }

        memberViewModel.isDormitoryChangeSuccess.observe(viewLifecycleOwner) {
            if (it) {
                memberViewModel.requestUserInfo() // Home에서 갱신된 User Dormitory를 인식할 수 있도록 유저 정보 새로고침
                recruitViewModel.requestHomeRecruitTagList(sort = "deadLineTime") // Dormitory 정보에 따라 상단 글 리스트도 바뀌므로 이를 반영
                memberViewModel.resetChangeUserDormitorySuccess() // TODO: true로 받아온 경우 다시 false로 바꿔주면서 나가야 하므로 임시 처리
                findNavController().navigateUp()
            }
        }
    }
}