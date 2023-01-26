package com.mate.baedalmate.data.datasource

import android.accounts.NetworkErrorException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mate.baedalmate.data.datasource.remote.search.SearchApiService
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.model.setExceptionHandling
import kotlinx.coroutines.delay

private const val POST_LIST_STARTING_PAGE_INDEX = 0
private const val DELAY_MILLIS = 1_000L

class PostSearchResultPagingSource(
    private val searchApiService: SearchApiService,
    private val keyword: String,
    private val sort: String
) :
    PagingSource<Int, Pair<RecruitDto, Int>>() {
    override fun getRefreshKey(state: PagingState<Int, Pair<RecruitDto, Int>>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pair<RecruitDto, Int>> {
        val position = params.key ?: POST_LIST_STARTING_PAGE_INDEX
        if (position != POST_LIST_STARTING_PAGE_INDEX) delay(DELAY_MILLIS)

        return try {
            val response = setExceptionHandling {
                searchApiService.requestGetSearchTagKeyword(
                    keyword = keyword,
                    page = position,
                    size = 6,
                    sort = sort
                )
            }
            when (response.status) {
                ApiResult.Status.SUCCESS -> {
                    response.data?.let { searchRecruitList ->
                        LoadResult.Page(
                            data = searchRecruitList.recruitList.map {
                                Pair(it, response.data.total)
                            },
                            prevKey = null, // Only Paging Forward
                            nextKey = if (response.data.last) null else position + 1
                        )
                    } ?: LoadResult.Page(data = emptyList(), null, null)
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