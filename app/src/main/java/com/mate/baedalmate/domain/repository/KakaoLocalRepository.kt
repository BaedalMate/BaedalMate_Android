package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.write.ResultSearchKeyword
import retrofit2.Response

interface KakaoLocalRepository {
    suspend fun getKakaoLocalSearchKeyword(url: String, key: String, query: String, x: String, y: String): Response<ResultSearchKeyword>
}