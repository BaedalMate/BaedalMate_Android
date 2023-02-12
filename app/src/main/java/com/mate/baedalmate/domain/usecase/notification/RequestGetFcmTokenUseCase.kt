package com.mate.baedalmate.domain.usecase.notification

import com.mate.baedalmate.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetFcmTokenUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke() = notificationRepository.getCurrentFcmToken()
}