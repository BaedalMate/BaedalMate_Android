package com.mate.baedalmate.domain.usecase.chat

import com.mate.baedalmate.domain.model.OrderDto
import com.mate.baedalmate.domain.repository.ChatRepository
import javax.inject.Inject

class RequestPutChangeMyMenuListUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(data: OrderDto) = chatRepository.requestPutChangeMyMenuList(data = data)
}