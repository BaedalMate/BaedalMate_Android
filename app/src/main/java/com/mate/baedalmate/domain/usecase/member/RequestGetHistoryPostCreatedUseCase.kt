package com.mate.baedalmate.domain.usecase.member

import com.mate.baedalmate.domain.repository.MemberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetHistoryPostCreatedUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(page: Int, size: Int, sort: String) =
        memberRepository.requestGetHistoryPostCreated(
            page = page,
            size = size,
            sort = sort
        )
}