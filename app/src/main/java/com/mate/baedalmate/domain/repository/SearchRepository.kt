package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.RecruitList

interface SearchRepository {
    suspend fun requestGetSearchTagKeyword(
        keyword: String,
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<RecruitList>
}