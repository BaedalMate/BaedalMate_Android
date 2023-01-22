package com.mate.baedalmate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mate.baedalmate.data.datasource.PostPagingSource
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitApiService
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitDetailForModify
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.RecruitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecruitRepositoryImpl @Inject constructor(private val recruitApiService: RecruitApiService) :
    RecruitRepository {
    override suspend fun requestRecruitList(
        categoryId: Int?,
        sort: String
    ): Flow<PagingData<RecruitDto>> =
        Pager(
            config = PagingConfig(pageSize = 6, maxSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { PostPagingSource(recruitApiService, categoryId, sort) }
        ).flow

    override suspend fun requestRecruitMainList(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<MainRecruitList> =
        setExceptionHandling {
            recruitApiService.requestRecruitMainList(page = page, size = size, sort = sort)
        }

    override suspend fun requestRecruitTagList(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<TagRecruitList> =
        setExceptionHandling {
            recruitApiService.requestRecruitTagList(
                page = page,
                size = size,
                sort = sort
            )
        }

    override suspend fun requestRecruitPost(id: Int): ApiResult<RecruitDetail> =
        setExceptionHandling {
            recruitApiService.requestRecruitPost(id = id)
        }

    override suspend fun requestRecruitPostDetailForModify(id: Int): ApiResult<RecruitDetailForModify> =
        setExceptionHandling {
            recruitApiService.requestRecruitPostDetailForModify(id = id)
        }

    override suspend fun requestCancelRecruitPost(id: Int): ApiResult<Void> =
        setExceptionHandling {
            recruitApiService.requestCancelRecruitPost(id = id)
        }

    override suspend fun requestCloseRecruitPost(id: Int): ApiResult<Void> =
        setExceptionHandling {
            recruitApiService.requestCloseRecruitPost(id = id)
        }

    override suspend fun requestParticipateRecruitPost(data: CreateOrderRequest): ApiResult<CreateOrderResponse> =
        setExceptionHandling {
            recruitApiService.requestParticipateRecruitPost(data = data)
        }

    override suspend fun requestCancelParticipateRecruitPost(data: DeleteOrderDto): ApiResult<Void> =
        setExceptionHandling {
            recruitApiService.requestCancelParticipateRecruitPost(data = data)
        }
}