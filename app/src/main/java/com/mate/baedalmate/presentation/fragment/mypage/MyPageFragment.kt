package com.mate.baedalmate.presentation.fragment.mypage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.ConfirmAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentMyPageBinding
import com.mate.baedalmate.presentation.activity.MainActivity
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var binding by autoCleared<FragmentMyPageBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var logoutAlertDialog: AlertDialog
    private lateinit var resignAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
        createLogoutAlertDialog()
        createResignAlertDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        getUserData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserData()
        setMenusSettingClickListener()
        setMenusInfoClickListener()
        setAccountActionClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ConfirmAlertDialog.hideConfirmDialog(logoutAlertDialog)
        ConfirmAlertDialog.hideConfirmDialog(resignAlertDialog)
    }

    private fun getUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memberViewModel.requestUserInfo()
            }
        }
    }

    private fun initUserData() {
        memberViewModel.userInfo.observe(viewLifecycleOwner) { info ->
            glideRequestManager.load("http://3.35.27.107:8080/images/${info.profileImage}")
                .override(GetDeviceSize.getDeviceWidthSize(requireContext()))
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgMyPageUserInfo)

            binding.tvMyPageUserInfoNickname.text = info.nickname
            binding.tvMyPageUserInfoDormitory.text = "${info.dormitory}"
            binding.tvMyPageUserInfoScore.text = info.score.toString()
        }
    }

    private fun setMenusSettingClickListener() {
        // TODO 알림 설정
        binding.layoutMyPageMenusSettingLocationChange.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_locationCertificationFragment)
        }

        binding.layoutMyPageMenusSettingBlock.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_blockUserListFragment)
        }
    }

    private fun setMenusInfoClickListener() {
        binding.layoutMyPageMenusInfoNotice.setOnDebounceClickListener {
            // TODO 공지사항 navigation
        }
        binding.layoutMyPageMenusInfoInquiry.setOnDebounceClickListener {
            // TODO 문의하기 클릭
        }
    }

    private fun setAccountActionClickListener() {
        binding.tvMyPageAccountActionLogout.setOnDebounceClickListener {
            ConfirmAlertDialog.showConfirmDialog(logoutAlertDialog)
            ConfirmAlertDialog.resizeDialogFragment(
                requireContext(),
                logoutAlertDialog,
                dialogSizeRatio = 0.7f
            )
        }
        binding.tvMyPageAccountActionResign.setOnDebounceClickListener {
            ConfirmAlertDialog.showConfirmDialog(resignAlertDialog)
            ConfirmAlertDialog.resizeDialogFragment(
                requireContext(),
                resignAlertDialog,
                dialogSizeRatio = 0.7f
            )
        }
    }

    private fun createLogoutAlertDialog() {
        logoutAlertDialog = ConfirmAlertDialog.createChoiceDialog(
            context = requireContext(),
            title = getString(R.string.logout_dialog_title),
            description = getString(R.string.logout_dialog_description),
            confirmButtonFunction = {
                // TODO 로그아웃 기능 동작 추가
                clearLocalData()
                navigateToLoginActivity()
            }
        )
    }

    private fun createResignAlertDialog() {
        resignAlertDialog = ConfirmAlertDialog.createChoiceDialog(
            context = requireContext(),
            title = getString(R.string.resign_dialog_title),
            description = getString(R.string.resign_dialog_description),
            confirmButtonFunction = {
                // TODO 탈퇴 기능 동작 추가
                clearLocalData()
                navigateToLoginActivity()
            }
        )
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    private fun clearLocalData() {
        // TODO 로컬 저장된 정보 삭제
    }
}