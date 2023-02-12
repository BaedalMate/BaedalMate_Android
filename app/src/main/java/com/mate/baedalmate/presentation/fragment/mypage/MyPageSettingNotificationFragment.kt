package com.mate.baedalmate.presentation.fragment.mypage

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentMyPageSettingNotificationBinding
import com.mate.baedalmate.presentation.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageSettingNotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentMyPageSettingNotificationBinding>()
    private val notificationViewModel by activityViewModels<NotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageSettingNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        initNotificationStates()
        setNotificationAll()
        setNotificationNewMessage()
        setNotificationRecruit()
        setNotificationNotice()
    }

    private fun setBackClickListener() {
        binding.btnMyPageSettingNotificationActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initNotificationStates() {
        // 전체 알림 먼저 확인한뒤 하위 알림들 확인
        if (notificationViewModel.getNotificationSettings(getString(R.string.notification_type_all))) {
            with(binding) {
                btnToggleMyPageMenusSettingNotificationAll.isChecked =
                    notificationViewModel.getNotificationSettings(getString(R.string.notification_type_all))
                btnToggleMyPageMenusSettingNotificationNewMessage.isChecked =
                    notificationViewModel.getNotificationSettings(getString(R.string.notification_type_newMessage))
                btnToggleMyPageMenusSettingNotificationRecruit.isChecked =
                    notificationViewModel.getNotificationSettings(getString(R.string.notification_type_recruit))
                btnToggleMyPageMenusSettingNotificationNotice.isChecked =
                    notificationViewModel.getNotificationSettings(getString(R.string.notification_type_notice))
            }
        } else {
            showNotificationStates(isOnState = false)
        }
    }

    private fun setNotificationAll() {
        binding.btnToggleMyPageMenusSettingNotificationAll.setOnCheckedChangeListener { _, isChecked ->
            // 모든 알림 설정이 ON/OFF 될때 하위 알림들도 모두 따라가도록 설정
            showNotificationStates(isOnState = isChecked)
            notificationViewModel.setNotificationAll(isChecked)
            if (isChecked) notificationViewModel.registerFcmToken(requireContext().contentResolver)
            else notificationViewModel.unregisterFcmToken()
        }
    }

    private fun showNotificationStates(isOnState: Boolean) {
        with(binding) {
            btnToggleMyPageMenusSettingNotificationNewMessage.isEnabled = isOnState
            btnToggleMyPageMenusSettingNotificationRecruit.isEnabled = isOnState
            btnToggleMyPageMenusSettingNotificationNotice.isEnabled = isOnState
        }
        with(binding) {
            btnToggleMyPageMenusSettingNotificationNewMessage.isChecked = isOnState
            btnToggleMyPageMenusSettingNotificationRecruit.isChecked = isOnState
            btnToggleMyPageMenusSettingNotificationNotice.isChecked = isOnState
        }
    }

    private fun setNotificationNewMessage() {
        binding.btnToggleMyPageMenusSettingNotificationNewMessage.setOnCheckedChangeListener { _, isChecked ->
            notificationViewModel.setNotificationNewMessage(isChecked)
        }
    }

    private fun setNotificationRecruit() {
        binding.btnToggleMyPageMenusSettingNotificationRecruit.setOnCheckedChangeListener { _, isChecked ->
            notificationViewModel.setNotificationRecruit(isChecked)
        }
    }

    private fun setNotificationNotice() {
        binding.btnToggleMyPageMenusSettingNotificationNotice.setOnCheckedChangeListener { _, isChecked ->
            notificationViewModel.setNotificationNotice(isChecked)
        }
    }

    // 설정으로 이동해 알림을 끄는 방법
    private fun initNotification() {
        val currentNotificationState =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                Intent().apply {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", requireContext().packageName)
                    putExtra("app_uid", requireContext().applicationInfo?.uid)
                }
            }
            else -> {
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("package:${requireContext().packageName}")
                }
            }
        }

        binding.btnToggleMyPageMenusSettingNotificationAll.isChecked = currentNotificationState
        binding.btnToggleMyPageMenusSettingNotificationAll.setOnTouchListener { _, _ ->
            binding.btnToggleMyPageMenusSettingNotificationAll.isClickable = false
            startActivity(intent)
            false
        }
    }
}