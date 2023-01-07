package com.mate.baedalmate.domain.usecase.member

import com.mate.baedalmate.domain.model.UpdateUserDto
import com.mate.baedalmate.domain.repository.MemberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPutChangeMyProfileUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(updateUserInfo: UpdateUserDto) =
        memberRepository.requestPutChangeMyProfile(updateUserInfo = updateUserInfo)
}