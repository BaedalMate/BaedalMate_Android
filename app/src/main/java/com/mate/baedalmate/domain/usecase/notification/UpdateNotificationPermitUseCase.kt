package com.mate.baedalmate.domain.usecase.notification

import com.mate.baedalmate.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateNotificationPermitUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke(deviceCode: String, allowChat: Boolean, allowRecruit: Boolean) =
        notificationRepository.updateNotificationPermit(
            deviceCode = deviceCode,
            allowChat = allowChat,
            allowRecruit = allowRecruit
        )
}