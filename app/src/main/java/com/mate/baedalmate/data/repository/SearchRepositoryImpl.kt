package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.search.SearchApiService
import com.mate.baedalmate.domain.model.RecruitList
import com.mate.baedalmate.domain.repository.SearchRepository
import retrofit2.Response
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchApiService: SearchApiService) :
    SearchRepository {
    override suspend fun requestGetSearchTagKeyword(
        keyword: String,
        page: Int,
        size: Int,
        sort: String
    ): Response<RecruitList> = searchApiService.requestGetSearchTagKeyword(
        keyword = keyword, page = page, size = size, sort = sort
    )
}