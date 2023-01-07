package com.mate.baedalmate.domain.model

import retrofit2.Response
import java.net.URLDecoder

data class ApiResult<out T>(
    val status: Status,
    val code: String?,
    val message: String?,
    val data: T?,
    val exception: Exception?
) {
    enum class Status {
        SUCCESS,
        API_ERROR,
        NETWORK_ERROR,
        LOADING
    }

    companion object {
        fun <T> success(code: String, data: T?): ApiResult<T> =
            ApiResult(Status.SUCCESS, code, "", data, null)

        fun <T> error(code: String, message: String): ApiResult<T> =
            ApiResult(
                Status.API_ERROR,
                URLDecoder.decode(code, "UTF-8"),
                URLDecoder.decode(message, "UTF-8"),
                null,
                null
            )

        fun <T> error(exception: Exception?): ApiResult<T> =
            ApiResult(Status.NETWORK_ERROR, null, null, null, exception)

        fun <T> loading(): ApiResult<T> =
            ApiResult(Status.LOADING, null, null, null, null)
    }

    override fun toString(): String {
        return "Result(status=$status, code=$code, message=$message, data=$data, error=$exception)"
    }
}

fun <T> Response<T>.toApiResult(): ApiResult<T> {
    return if (this.isSuccessful) {
        return ApiResult.success("000000", this.body())
    } else {
        val code = this.code()
        val message = this.message() ?: ""
        ApiResult.error(code.toString(), message)
    }
}

suspend fun <T> setExceptionHandling(requestApi: suspend () -> Response<T>): ApiResult<T> =
    try {
        requestApi.invoke().toApiResult()
    } catch (e: Exception) {
        ApiResult.error(e)
    }