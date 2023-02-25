package com.mate.baedalmate.data.datasource.remote.write

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface KakaoLocalApiService {
    @GET()    // Keyword.json의 정보를 받아옴
    suspend fun getKakaoLocalSearchKeyword(
        @Url url: String,
        @Header("Authorization") key: String,     // 카카오 API 인증키 [필수]
        @Query("query") query: String,             // 검색을 원하는 질의어 [필수]
        @Query("x") x: String,             // 검색을 원하는 질의어 [필수]
        @Query("y") y: String,             // 검색을 원하는 질의어 [필수]
        @Query("sort") sort: String? = "distance"
        // 매개변수 추가 가능
        // @Query("category_group_code") category: String
    ): Response<ResultSearchKeyword>
}