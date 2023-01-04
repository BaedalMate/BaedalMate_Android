package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.domain.model.RecruitList
import retrofit2.Response

interface SearchRepository {
    suspend fun requestGetSearchTagKeyword(
        keyword: String,
        page: Int,
        size: Int,
        sort: String
    ): Response<RecruitList>
}