package com.mate.baedalmate.domain.usecase.report

import com.mate.baedalmate.data.datasource.remote.report.RecruitReportRequestDto
import com.mate.baedalmate.domain.repository.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPostReportRecruitUseCase @Inject constructor(private val reportRepository: ReportRepository) {
    suspend operator fun invoke(reportedRecruitInfo: RecruitReportRequestDto) =
        reportRepository.requestPostReportRecruit(
            data = reportedRecruitInfo
        )
}