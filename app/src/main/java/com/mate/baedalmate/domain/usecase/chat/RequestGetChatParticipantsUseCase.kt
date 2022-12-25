package com.mate.baedalmate.domain.usecase.chat

import com.mate.baedalmate.domain.repository.ChatRepository
import javax.inject.Inject

class RequestGetChatParticipantsUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(roomId: Int) = chatRepository.requestGetChatParticipants(roomId = roomId)
}