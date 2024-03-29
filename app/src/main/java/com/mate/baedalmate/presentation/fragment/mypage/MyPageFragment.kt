package com.mate.baedalmate.presentation.fragment.mypage

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.BuildConfig
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.ConfirmAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentMyPageBinding
import com.mate.baedalmate.presentation.activity.LoginActivity
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
        setUserInfoClickListener()
        setMenusSettingClickListener()
        setMenusInfoClickListener()
        setAppVersion()
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

    private fun setUserInfoClickListener() {
        binding.layoutMyPageUserInfo.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myProfileChangeFragment)
        }
        binding.tvMyPageUserInfoHistoryParticipated.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_historyPostParticipatedFragment)
        }
        binding.tvMyPageUserInfoHistoryCreated.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_historyPostCreatedFragment)
        }
    }

    private fun setMenusSettingClickListener() {
        with(binding) {
            layoutMyPageMenusSettingNotification.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_myPageSetNotificationFragment)
            }
            layoutMyPageMenusSettingMyProfileChange.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_myProfileChangeFragment)
            }
            layoutMyPageMenusSettingLocationChange.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_locationCertificationFragment)
            }
            layoutMyPageMenusSettingBlock.setOnDebounceClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_blockUserListFragment)
            }
        }
    }

    private fun setMenusInfoClickListener() {
        binding.layoutMyPageMenusInfoNotice.visibility = View.GONE
        binding.layoutMyPageMenusInfoNotice.setOnDebounceClickListener {
            // TODO 공지사항 navigation
        }
        setInquiryClickListener()
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
                setLogout()
            }
        )
    }

    private fun createResignAlertDialog() {
        resignAlertDialog = ConfirmAlertDialog.createChoiceDialog(
            context = requireContext(),
            title = getString(R.string.resign_dialog_title),
            description = getString(R.string.resign_dialog_description),
            confirmButtonFunction = {
                setResignUser()
            }
        )
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    private fun clearLocalData() {
        memberViewModel.requestClearAllLocalData()
    }

    private fun setLogout() {
        observeLogoutResult()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memberViewModel.requestLogout()
            }
        }
    }

    private fun observeLogoutResult() {
        memberViewModel.isLogoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                clearLocalData()
                navigateToLoginActivity()
                Toast.makeText(requireContext(), getString(R.string.logout_success_toast_message), Toast.LENGTH_SHORT).show()
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(requireContext(), getString(R.string.logout_fail_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setResignUser() {
        observeResignResult()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memberViewModel.requestResign()
            }
        }
    }

    private fun observeResignResult() {
        memberViewModel.isResignSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                clearLocalData()
                navigateToLoginActivity()
                Toast.makeText(requireContext(), getString(R.string.resign_success_toast_message), Toast.LENGTH_SHORT).show()
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(requireContext(), getString(R.string.resign_fail_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setInquiryClickListener() {
        binding.layoutMyPageMenusInfoInquiry.setOnDebounceClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf("baedalmate.official@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "[문의사항] 제목을 작성해주세요!")
                putExtra(
                    Intent.EXTRA_TEXT, String.format(
                        "[기본 사항] \nApp Version : ${BuildConfig.VERSION_NAME}\nDevice : ${Build.MODEL}\nAndroid(SDK) : ${Build.VERSION.SDK_INT}(${Build.VERSION.RELEASE})\n\n문의 내용 : 내용을 작성해주세요!"
                    )
                );
            }
            val chooser = Intent.createChooser(intent, "Select an email app to open")

            try {
                startActivity(chooser)
            } catch (e: ActivityNotFoundException) {
                e.stackTrace
            }
        }
    }

    private fun setAppVersion() {
        binding.tvMyPageMenusInfoAppVersionCurrent.text = BuildConfig.VERSION_NAME
    }
}