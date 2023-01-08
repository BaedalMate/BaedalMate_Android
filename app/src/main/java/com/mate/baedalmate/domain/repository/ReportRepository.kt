package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.report.RecruitReportRequestDto
import com.mate.baedalmate.data.datasource.remote.report.ReportResponse
import com.mate.baedalmate.data.datasource.remote.report.UserReportRequestDto
import com.mate.baedalmate.domain.model.ApiResult

interface ReportRepository {
    suspend fun requestPostReportUser(data: UserReportRequestDto): ApiResult<ReportResponse>
    suspend fun requestPostReportRecruit(data: RecruitReportRequestDto): ApiResult<ReportResponse>
}