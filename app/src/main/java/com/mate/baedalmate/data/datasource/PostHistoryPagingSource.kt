package com.mate.baedalmate.data.datasource

import android.accounts.NetworkErrorException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitResponseDto
import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.setExceptionHandling
import kotlinx.coroutines.delay

private const val POST_LIST_STARTING_PAGE_INDEX = 0
private const val DELAY_MILLIS = 1_000L

class PostHistoryPagingSource(
    private val memberApiService: MemberApiService,
    private val isHostingPosts: Boolean,
    private val sort: String
) : PagingSource<Int, HistoryRecruitResponseDto>() {
    override fun getRefreshKey(state: PagingState<Int, HistoryRecruitResponseDto>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryRecruitResponseDto> {
        val position = params.key ?: POST_LIST_STARTING_PAGE_INDEX
        if (position != POST_LIST_STARTING_PAGE_INDEX) delay(DELAY_MILLIS)

        return try {
            val response = setExceptionHandling {
                if (isHostingPosts) {
                    memberApiService.requestGetHistoryPostCreated(
                        page = position,
                        size = 6,
                        sort = sort
                    )
                } else {
                    memberApiService.requestGetHistoryPostParticipated(
                        page = position,
                        size = 6,
                        sort = sort
                    )
                }
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
                    response.exception?.let {
                        throw it
                    }
                    throw Exception("API REQUEST FAIL")
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}