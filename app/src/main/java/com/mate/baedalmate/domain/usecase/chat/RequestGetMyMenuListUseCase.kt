package com.mate.baedalmate.domain.usecase.chat

import com.mate.baedalmate.domain.repository.ChatRepository
import javax.inject.Inject

class RequestGetMyMenuListUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(id: Int) = chatRepository.requestGetMyMenuList(id = id)
}