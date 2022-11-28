package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitList
import retrofit2.Response

interface RecruitRepository {
    suspend fun requestRecruitList(
        categoryId: Int? = null,
        page: Int,
        size: Int,
        sort: String
    ): Response<RecruitList>

    suspend fun requestRecruitMainList(
        page: Int,
        size: Int,
        sort: String
    ): Response<MainRecruitList>

    suspend fun requestRecruitTagList(page: Int, size: Int, sort: String): Response<TagRecruitList>
    suspend fun requestRecruitPost(id: Int): Response<RecruitDetail>
    suspend fun requestCloseRecruitPost(id: Int): Response<Void>
    suspend fun requestParticipateRecruitPost(data: CreateOrderRequest): Response<CreateOrderResponse>
    suspend fun requestCancelParticipateRecruitPost(data: DeleteOrderDto): Response<Void>
}