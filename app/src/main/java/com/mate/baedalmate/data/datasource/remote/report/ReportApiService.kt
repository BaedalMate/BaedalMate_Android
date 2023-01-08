package com.mate.baedalmate.data.datasource.remote.report

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApiService {
    @POST("/api/v1/report/user")
    suspend fun requestPostReportUser(@Body data: UserReportRequestDto): Response<ReportResponse>
    @POST("/api/v1/report/recruit")
    suspend fun requestPostReportRecruit(@Body data: RecruitReportRequestDto): Response<ReportResponse>
}