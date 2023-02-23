package com.mate.baedalmate.presentation.viewmodel

import android.content.ContentResolver
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.data.datasource.remote.notification.FcmAllow
import com.mate.baedalmate.data.datasource.remote.notification.NotificationList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import com.mate.baedalmate.domain.usecase.notification.GetNotificationPermitUseCase
import com.mate.baedalmate.domain.usecase.notification.RegisterFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.notification.RequestGetFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.notification.RequestNotificationListUseCase
import com.mate.baedalmate.domain.usecase.notification.SubscribeTopicNoticeUseCase
import com.mate.baedalmate.domain.usecase.notification.UnregisterFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.notification.UnsubscribeTopicNoticeUseCase
import com.mate.baedalmate.domain.usecase.notification.UpdateNotificationPermitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val unregisterFcmTokenUseCase: UnregisterFcmTokenUseCase,
    private val requestGetFcmTokenUseCase: RequestGetFcmTokenUseCase,
    private val subscribeTopicNoticeUseCase: SubscribeTopicNoticeUseCase,
    private val unsubscribeTopicNoticeUseCase: UnsubscribeTopicNoticeUseCase,
    private val getNotificationPermitUseCase: GetNotificationPermitUseCase,
    private val updateNotificationPermitUseCase: UpdateNotificationPermitUseCase,
    private val requestNotificationListUseCase: RequestNotificationListUseCase,
    private val tokenPreferencesRepository: TokenPreferencesRepository
) : ViewModel() {
    private val _notifications = MutableLiveData<NotificationList>()
    val notifications: LiveData<NotificationList> get() = _notifications

    private val _notificationPermits = MutableLiveData<FcmAllow>()
    val notificationPermits: LiveData<FcmAllow> get() = _notificationPermits

    private val _isUpdateNotificationPermitSuccess = MutableLiveData<Boolean>(true)
    val isUpdateNotificationPermitSuccess: LiveData<Boolean> get() = _isUpdateNotificationPermitSuccess

    fun registerFcmToken(contentResolver: ContentResolver) = viewModelScope.launch {
        registerFcmTokenUseCase(
            fcmToken = requestGetFcmTokenUseCase(),
            deviceCode = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        )
    }

    fun unregisterFcmToken() = viewModelScope.launch {
        unregisterFcmTokenUseCase()
    }

    fun setNotificationAll(isPermit: Boolean) = viewModelScope.launch {
        tokenPreferencesRepository.setNotificationPermitAll(isPermit)
    }

    fun setNotificationNewMessage(isPermit: Boolean, currentRecruitAllowed: Boolean, contentResolver: ContentResolver) = viewModelScope.launch {
        updateNotificationPermit(contentResolver = contentResolver, allowChat = isPermit, allowRecruit = currentRecruitAllowed)
        tokenPreferencesRepository.setNotificationPermitNewMessage(isPermit)
    }

    fun setNotificationRecruit(isPermit: Boolean, currentMessageAllowed: Boolean, contentResolver: ContentResolver) = viewModelScope.launch {
        updateNotificationPermit(contentResolver = contentResolver, allowChat = currentMessageAllowed, allowRecruit = isPermit)
        tokenPreferencesRepository.setNotificationPermitRecruit(isPermit)
    }

    fun setNotificationNotice(isPermit: Boolean) = viewModelScope.launch {
        // 서버에서 받아오지 않도록 구독 취소
        if (isPermit) subscribeTopicNoticeUseCase()
        else unsubscribeTopicNoticeUseCase()

        // 내부 저장소에 설정값 저장
        tokenPreferencesRepository.setNotificationPermitNotice(isPermit)
    }

    fun getNotificationSettings(notificationType: String): Boolean {
        var isPermit = false
        viewModelScope.launch {
            isPermit = when (notificationType) {
                "all" -> tokenPreferencesRepository.getNotificationPermitAll()
                "newMessage" -> {
                    tokenPreferencesRepository.getNotificationPermitNewMessage()
                }
                "recruit" -> tokenPreferencesRepository.getNotificationPermitRecruit()
                "notice" -> tokenPreferencesRepository.getNotificationPermitNotice()
                else -> false
            }
        }
        return isPermit
    }

    fun getNotifications() = viewModelScope.launch {
        requestNotificationListUseCase()?.let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data?.let { notificationsResponse ->
                        _notifications.postValue(notificationsResponse)
                    }
                }
                else -> {

                }
            }
        }
    }

    fun syncNotificationPermit(contentResolver: ContentResolver) = viewModelScope.launch {
        getNotificationPermitUseCase(
            deviceCode = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        )?.let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data?.let { permitAllows ->
                        tokenPreferencesRepository.setNotificationPermitNewMessage(isPermit = permitAllows.allowChat)
                        tokenPreferencesRepository.setNotificationPermitRecruit(isPermit = permitAllows.allowRecruit)
                    }
                }
                else -> {}
            }
        }
    }

    private fun updateNotificationPermit(
        contentResolver: ContentResolver,
        allowChat: Boolean,
        allowRecruit: Boolean
    ) = viewModelScope.launch {
        updateNotificationPermitUseCase(
            deviceCode = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
            allowChat = allowChat, allowRecruit = allowRecruit
        )?.let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isUpdateNotificationPermitSuccess.postValue(true)
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    _isUpdateNotificationPermitSuccess.postValue(false)
                }
                ApiResult.Status.API_ERROR -> {
                    _isUpdateNotificationPermitSuccess.postValue(false)
                }
            }
        }
    }
}