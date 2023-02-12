package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.notification.NotificationList
import com.mate.baedalmate.domain.model.ApiResult

interface NotificationRepository {
    suspend fun requestNotifications(): ApiResult<NotificationList>

    // Firebase Messaging Service
    suspend fun getCurrentFcmToken(): String
    suspend fun registerFcmToken(fcmToken: String, deviceCode: String)
    suspend fun unregisterFcmToken()
    suspend fun subscribeTopicNotice()
    suspend fun unsubscribeTopicNotice()
}