package com.mate.baedalmate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mate.baedalmate.data.datasource.PostSearchResultPagingSource
import com.mate.baedalmate.data.datasource.remote.search.SearchApiService
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchApiService: SearchApiService) :
    SearchRepository {
    override suspend fun requestGetSearchTagKeyword(
        keyword: String,
        sort: String
    ): Flow<PagingData<RecruitDto>> = Pager(
        config = PagingConfig(pageSize = 6, maxSize = 30, enablePlaceholders = false),
        pagingSourceFactory = { PostSearchResultPagingSource(searchApiService, keyword, sort) }
    ).flow
}