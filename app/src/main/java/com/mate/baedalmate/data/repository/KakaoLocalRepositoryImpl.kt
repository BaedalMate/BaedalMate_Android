package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.write.KakaoLocalApiService
import com.mate.baedalmate.data.datasource.remote.write.ResultSearchKeyword
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.KakaoLocalRepository
import retrofit2.Response
import javax.inject.Inject

class KakaoLocalRepositoryImpl @Inject constructor(private val kakaoLocalApiService: KakaoLocalApiService) :
    KakaoLocalRepository {
    override suspend fun getKakaoLocalSearchKeyword(
        url: String,
        key: String,
        query: String,
        x: String,
        y: String
    ): ApiResult<ResultSearchKeyword> =
        setExceptionHandling {
            kakaoLocalApiService.getKakaoLocalSearchKeyword(
                url = url,
                key = key,
                query = query,
                x = x,
                y = y
            )
        }
}