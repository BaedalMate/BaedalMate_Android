package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.report.RecruitReportRequestDto
import com.mate.baedalmate.data.datasource.remote.report.ReportApiService
import com.mate.baedalmate.data.datasource.remote.report.ReportResponse
import com.mate.baedalmate.data.datasource.remote.report.UserReportRequestDto
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(private val reportApiService: ReportApiService) :
    ReportRepository {
    override suspend fun requestPostReportUser(data: UserReportRequestDto): ApiResult<ReportResponse> =
        setExceptionHandling { reportApiService.requestPostReportUser(data = data) }

    override suspend fun requestPostReportRecruit(data: RecruitReportRequestDto): ApiResult<ReportResponse> =
        setExceptionHandling { reportApiService.requestPostReportRecruit(data = data) }
}