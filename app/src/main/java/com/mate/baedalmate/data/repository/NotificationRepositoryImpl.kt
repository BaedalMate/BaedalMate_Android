package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.firebase.FirebaseMessagingService
import com.mate.baedalmate.data.datasource.remote.notification.FcmAllow
import com.mate.baedalmate.data.datasource.remote.notification.NotificationApiService
import com.mate.baedalmate.data.datasource.remote.notification.NotificationList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApiService: NotificationApiService,
    private val firebaseMessagingService: FirebaseMessagingService
) : NotificationRepository {
    override suspend fun requestNotifications(): ApiResult<NotificationList> =
        setExceptionHandling { notificationApiService.requestGetNotifications() }

    override suspend fun getNotificationPermit(deviceCode: String): ApiResult<FcmAllow> =
        setExceptionHandling { notificationApiService.requestGetFcmPermit(deviceCode = deviceCode) }

    override suspend fun updateNotificationPermit(
        deviceCode: String,
        allowChat: Boolean,
        allowRecruit: Boolean
    ): ApiResult<FcmAllow> =
        setExceptionHandling {
            notificationApiService.requestPutUpdateFcmPermit(
                deviceCode = deviceCode,
                allow_chat = allowChat,
                allow_recruit = allowRecruit
            )
        }

    // Firebase Messaging Service
    override suspend fun getCurrentFcmToken(): String =
        firebaseMessagingService.getCurrentToken()

    override suspend fun registerFcmToken(fcmToken: String, deviceCode: String) {
        firebaseMessagingService.registerFcmToken()
        setExceptionHandling {
            notificationApiService.requestPostRegisterFcmToken(
                fcmToken = fcmToken,
                deviceCode = deviceCode
            )
        }
    }

    override suspend fun unregisterFcmToken() =
        firebaseMessagingService.unregisterFcmToken()

    override suspend fun subscribeTopicNotice() =
        firebaseMessagingService.subscribeTopicNotice()

    override suspend fun unsubscribeTopicNotice() =
        firebaseMessagingService.unsubscribeTopicNotice()
}