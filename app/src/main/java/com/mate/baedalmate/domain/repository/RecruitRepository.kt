package com.mate.baedalmate.domain.repository

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
}