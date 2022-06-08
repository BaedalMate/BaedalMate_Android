package com.mate.baedalmate.common.base

import retrofit2.Response
import com.mate.baedalmate.data.datasource.ApiResult

open class BaseRepository {
    internal suspend fun <T> getResponse(response: Response<T>): ApiResult<T> {
        return try {
            if (response.isSuccessful) {
                return ApiResult.success("000000", response.body())
            } else {
                val code = response.code()
                val message = response.message() ?: ""
                ApiResult.error(code.toString(), message)
            }
        } catch (e: Exception) {
            ApiResult.error(e)
        }
    }
}
