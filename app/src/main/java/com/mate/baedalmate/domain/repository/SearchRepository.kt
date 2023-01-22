package com.mate.baedalmate.domain.repository

import androidx.paging.PagingData
import com.mate.baedalmate.domain.model.RecruitDto
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun requestGetSearchTagKeyword(
        keyword: String,
        sort: String
    ): Flow<PagingData<RecruitDto>>
}