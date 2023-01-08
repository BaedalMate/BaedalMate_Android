package com.mate.baedalmate.domain.usecase.report

import com.mate.baedalmate.data.datasource.remote.report.UserReportRequestDto
import com.mate.baedalmate.domain.repository.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPostReportUserUseCase @Inject constructor(private val reportRepository: ReportRepository) {
    suspend operator fun invoke(reportedUserInfo: UserReportRequestDto) =
        reportRepository.requestPostReportUser(
            data = reportedUserInfo
        )
}