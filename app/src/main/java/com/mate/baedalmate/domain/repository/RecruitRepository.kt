package com.mate.baedalmate.domain.repository

import androidx.paging.PagingData
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitDto
import kotlinx.coroutines.flow.Flow

interface RecruitRepository {
    suspend fun requestRecruitList(categoryId: Int?, sort: String): Flow<PagingData<RecruitDto>>
    suspend fun requestRecruitMainList(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<MainRecruitList>

    suspend fun requestRecruitTagList(page: Int, size: Int, sort: String): ApiResult<TagRecruitList>
    suspend fun requestRecruitPost(id: Int): ApiResult<RecruitDetail>
    suspend fun requestCancelRecruitPost(id: Int): ApiResult<Void>
    suspend fun requestCloseRecruitPost(id: Int): ApiResult<Void>
    suspend fun requestParticipateRecruitPost(data: CreateOrderRequest): ApiResult<CreateOrderResponse>
    suspend fun requestCancelParticipateRecruitPost(data: DeleteOrderDto): ApiResult<Void>
}