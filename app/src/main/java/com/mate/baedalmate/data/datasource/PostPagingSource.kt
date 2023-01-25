package com.mate.baedalmate.data.datasource

import android.accounts.NetworkErrorException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitApiService
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.model.setExceptionHandling
import kotlinx.coroutines.delay

private const val POST_LIST_STARTING_PAGE_INDEX = 0
private const val DELAY_MILLIS = 1_000L

class PostPagingSource(
    private val recruitApiService: RecruitApiService,
    private val categoryId: Int?,
    private val exceptClose: Boolean,
    private val sort: String
) : PagingSource<Int, RecruitDto>() {
    override fun getRefreshKey(state: PagingState<Int, RecruitDto>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecruitDto> {
        val position = params.key ?: POST_LIST_STARTING_PAGE_INDEX
        if (position != POST_LIST_STARTING_PAGE_INDEX) delay(DELAY_MILLIS)

        return try {
            val response = setExceptionHandling {
                recruitApiService.requestRecruitList(
                    categoryId = categoryId,
                    except_close = exceptClose,
                    page = position,
                    size = 6,
                    sort = sort
                )
            }
            when (response.status) {
                ApiResult.Status.SUCCESS -> {
                    LoadResult.Page(
                        data = response.data!!.recruitList,
                        prevKey = null, // Only Paging Forward
                        nextKey = if (response.data.last) null else position + 1
                    )
                }
                else -> {
                    throw NetworkErrorException("API RESULT FAIL")
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}