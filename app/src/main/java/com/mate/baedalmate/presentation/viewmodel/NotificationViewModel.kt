package com.mate.baedalmate.presentation.viewmodel

import android.content.ContentResolver
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.data.datasource.remote.notification.NotificationList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import com.mate.baedalmate.domain.usecase.notification.RegisterFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.notification.RequestGetFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.notification.RequestNotificationListUseCase
import com.mate.baedalmate.domain.usecase.notification.SubscribeTopicNoticeUseCase
import com.mate.baedalmate.domain.usecase.notification.UnregisterFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.notification.UnsubscribeTopicNoticeUseCase
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
    private val requestNotificationListUseCase: RequestNotificationListUseCase,
    private val tokenPreferencesRepository: TokenPreferencesRepository
) : ViewModel() {
    private val _notifications = MutableLiveData<NotificationList>()
    val notifications: LiveData<NotificationList> get() = _notifications

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

    fun setNotificationNewMessage(isPermit: Boolean) = viewModelScope.launch {
        // TODO 서버에 새로운 메시지 관련 알림 수신/미수신 요청
        tokenPreferencesRepository.setNotificationPermitNewMessage(isPermit)
    }

    fun setNotificationRecruit(isPermit: Boolean) = viewModelScope.launch {
        // TODO 서버에 모집글 알림 수신/미수신 요청
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
                "newMessage" -> tokenPreferencesRepository.getNotificationPermitNewMessage()
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
}