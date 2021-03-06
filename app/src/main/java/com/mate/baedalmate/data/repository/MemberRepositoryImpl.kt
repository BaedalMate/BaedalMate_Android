package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.domain.repository.MemberRepository
import retrofit2.Response
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(private val memberApiService: MemberApiService) :
    MemberRepository {
    override suspend fun requestLoginKakao(data: MemberOAuthRequest): Response<MemberOAuthResponse> {
        return memberApiService.requestLoginKakao(data)
    }
}