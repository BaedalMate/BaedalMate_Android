package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.review.CreateReviewDto
import com.mate.baedalmate.domain.model.ApiErrorStatus
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.UserDto
import com.mate.baedalmate.domain.usecase.review.RequestGetTargetReviewUserListUseCase
import com.mate.baedalmate.domain.usecase.review.RequestReviewUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val getTargetReviewUserListUseCase: RequestGetTargetReviewUserListUseCase,
    private val reviewUsersUseCase: RequestReviewUsersUseCase
) : ViewModel() {
    private val _reviewTargetUserList = MutableLiveData(ParticipantsDto(emptyList(), 0))
    val reviewTargetUserList: LiveData<ParticipantsDto> get() = _reviewTargetUserList

    private val _isReviewSubmitSuccess = MutableLiveData<Event<ApiErrorStatus>>()
    val isReviewSubmitSuccess: LiveData<Event<ApiErrorStatus>> get() = _isReviewSubmitSuccess

    fun requestGetTargetReviewUserList(recruitId: Int) = viewModelScope.launch {
        getTargetReviewUserListUseCase.invoke(recruitId = recruitId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _reviewTargetUserList.postValue(ApiResponse.data)
                }
            }
        }
    }

    fun requestCreateReviewUsers(participatedUsers: List<UserDto>, recruitId: Int) =
        viewModelScope.launch {
            reviewUsersUseCase.invoke(body = CreateReviewDto(participatedUsers, recruitId))
                .let { ApiResponse ->
                    when (ApiResponse.status) {
                        ApiResult.Status.SUCCESS -> {
                            _isReviewSubmitSuccess.postValue(Event(ApiErrorStatus.RESPONSE_SUCCESS))
                        }
                        ApiResult.Status.API_ERROR -> {
                            when (ApiResponse.code) {
                                "400" -> _isReviewSubmitSuccess.postValue(Event(ApiErrorStatus.RESPONSE_FAIL_DUPLICATE))
                                else -> _isReviewSubmitSuccess.postValue(Event(ApiErrorStatus.RESPONSE_FAIL_UNKNOWN))
                            }
                        }
                    }
                }
        }
}