package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.report.RecruitReportRequestDto
import com.mate.baedalmate.data.datasource.remote.report.UserReportRequestDto
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.usecase.report.RequestPostReportRecruitUseCase
import com.mate.baedalmate.domain.usecase.report.RequestPostReportUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val requestPostReportUserUseCase: RequestPostReportUserUseCase,
    private val requestPostReportRecruitUseCase: RequestPostReportRecruitUseCase
) :
    ViewModel() {
    private val _isSuccessReportUser = MutableLiveData<Event<Boolean>>()
    val isSuccessReportUser: LiveData<Event<Boolean>> get() = _isSuccessReportUser
    private val _isDuplicatedReportUser = MutableLiveData<Event<Boolean>>()
    val isDuplicatedReportUser: LiveData<Event<Boolean>> get() = _isDuplicatedReportUser

    private val _isSuccessReportRecruit = MutableLiveData<Event<Boolean>>()
    val isSuccessReportRecruit: LiveData<Event<Boolean>> get() = _isSuccessReportRecruit
    private val _isDuplicatedReportRecruit = MutableLiveData<Event<Boolean>>()
    val isDuplicatedReportRecruit: LiveData<Event<Boolean>> get() = _isDuplicatedReportRecruit

    fun requestPostReportUser(targetUserId: Int, reportReason: String, reportDetail: String) =
        viewModelScope.launch {
            requestPostReportUserUseCase(
                UserReportRequestDto(
                    targetUserId = targetUserId,
                    reason = reportReason,
                    detail = reportDetail
                )
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        ApiResponse.data.let { _isSuccessReportUser.postValue(Event(true)) }
                    }
                    ApiResult.Status.API_ERROR -> {
                        when (ApiResponse.code) {
                            "400" -> {
                                _isDuplicatedReportUser.postValue(Event(true))
                            }
                            else -> {
                                _isSuccessReportUser.postValue(Event(false))
                            }
                        }
                    }
                    ApiResult.Status.NETWORK_ERROR -> {
                        _isSuccessReportUser.postValue(Event(false))
                    }
                }
            }
        }

    fun requestPostReportRecruit(targetRecruitId: Int, reportReason: String, reportDetail: String) =
        viewModelScope.launch {
            requestPostReportRecruitUseCase(
                RecruitReportRequestDto(
                    targetRecruitId = targetRecruitId,
                    reason = reportReason,
                    detail = reportDetail
                )
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        ApiResponse.data.let { _isSuccessReportRecruit.postValue(Event(true)) }
                    }
                    ApiResult.Status.API_ERROR -> {
                        when (ApiResponse.code) {
                            "400" -> {
                                _isDuplicatedReportRecruit.postValue(Event(true))
                            }
                            else -> {
                                _isSuccessReportRecruit.postValue(Event(false))
                            }
                        }
                    }
                    ApiResult.Status.NETWORK_ERROR -> {
                        _isSuccessReportRecruit.postValue(Event(false))
                    }
                }
            }
        }
}