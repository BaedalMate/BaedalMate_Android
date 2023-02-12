package com.mate.baedalmate.data.datasource.remote.notification

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationApiService {
    @POST("/api/v1/fcm")
    suspend fun requestPostRegisterFcmToken(
        @Header("Fcm-Token") fcmToken: String,
        @Header("Device-Code") deviceCode: String,
    ): Response<Void>

    @GET("/api/v1/notification")
    suspend fun requestGetNotifications(): Response<NotificationList>
}