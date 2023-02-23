package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.notification.FcmAllow
import com.mate.baedalmate.data.datasource.remote.notification.NotificationList
import com.mate.baedalmate.domain.model.ApiResult

interface NotificationRepository {
    suspend fun requestNotifications(): ApiResult<NotificationList>
    suspend fun getNotificationPermit(deviceCode: String): ApiResult<FcmAllow>
    suspend fun updateNotificationPermit(
        deviceCode: String,
        allowChat: Boolean,
        allowRecruit: Boolean
    ): ApiResult<FcmAllow>

    // Firebase Messaging Service
    suspend fun getCurrentFcmToken(): String
    suspend fun registerFcmToken(fcmToken: String, deviceCode: String)
    suspend fun unregisterFcmToken()
    suspend fun subscribeTopicNotice()
    suspend fun unsubscribeTopicNotice()
}