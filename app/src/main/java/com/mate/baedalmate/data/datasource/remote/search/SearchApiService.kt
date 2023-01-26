package com.mate.baedalmate.data.datasource.remote.search

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("/api/v1/recruit/search")
    suspend fun requestGetSearchTagKeyword(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<SearchRecruitList>
}