package com.mate.baedalmate.domain.usecase.member

import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.domain.repository.MemberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestLoginKakaoUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(body: MemberOAuthRequest) =
        memberRepository.requestLoginKakao(body)
}