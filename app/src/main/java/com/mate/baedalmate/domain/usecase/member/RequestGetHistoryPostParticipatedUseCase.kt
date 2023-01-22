package com.mate.baedalmate.domain.usecase.member

import com.mate.baedalmate.domain.repository.MemberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetHistoryPostParticipatedUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(sort: String) =
        memberRepository.requestGetHistoryPostParticipated(sort = sort).flowOn(Dispatchers.Default)
}