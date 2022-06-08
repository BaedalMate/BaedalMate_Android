package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import retrofit2.Response

interface MemberRepository {
    suspend fun requestLoginKakao(body: MemberOAuthRequest): Response<MemberOAuthResponse>
}