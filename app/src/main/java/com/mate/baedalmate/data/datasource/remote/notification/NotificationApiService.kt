package com.mate.baedalmate.data.datasource.remote.notification

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface NotificationApiService {
    @GET("/api/v1/fcm")
    suspend fun requestGetFcmPermit(@Header("Device-Code") deviceCode: String): Response<FcmAllow>

    @POST("/api/v1/fcm")
    suspend fun requestPostRegisterFcmToken(
        @Header("Fcm-Token") fcmToken: String,
        @Header("Device-Code") deviceCode: String,
    ): Response<Void>

    @PUT("/api/v1/fcm")
    suspend fun requestPutUpdateFcmPermit(
        @Header("Device-Code") deviceCode: String,
        @Query("allow_chat") allow_chat: Boolean,
        @Query("allow_recruit") allow_recruit: Boolean,
    ): Response<FcmAllow>

    @GET("/api/v1/notification")
    suspend fun requestGetNotifications(): Response<NotificationList>
}