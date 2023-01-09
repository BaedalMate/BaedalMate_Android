package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.block.BlockRequestDto
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.usecase.block.RequestPostBlockUserUseCase
import com.mate.baedalmate.domain.usecase.block.RequestPostUnblockUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    private val requestPostBlockUserUseCase: RequestPostBlockUserUseCase,
    private val requestPostUnblockUserUseCase: RequestPostUnblockUserUseCase
) : ViewModel() {
    private val _isSuccessBlockUser = MutableLiveData<Event<Boolean>>()
    val isSuccessBlockUser: LiveData<Event<Boolean>> get() = _isSuccessBlockUser
    private val _isAlreadyBlockedUser = MutableLiveData<Event<Boolean>>()
    val isAlreadyBlockedUser: LiveData<Event<Boolean>> get() = _isAlreadyBlockedUser

    private val _isSuccessUnblockUser = MutableLiveData<Event<Boolean>>()
    val isSuccessUnblockUser: LiveData<Event<Boolean>> get() = _isSuccessUnblockUser

    fun requestPostBlockUser(blockUserId: Int) = viewModelScope.launch {
        requestPostBlockUserUseCase(
            BlockRequestDto(userId = blockUserId)
        ).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _isSuccessBlockUser.postValue(Event(true)) }
                }
                ApiResult.Status.API_ERROR -> {
                    when (ApiResponse.code) {
                        "400" -> {
                            _isAlreadyBlockedUser.postValue(Event(true))
                        }
                        else -> {
                            _isSuccessBlockUser.postValue(Event(false))
                        }
                    }
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    _isSuccessBlockUser.postValue(Event(false))
                }
            }
        }
    }

    fun requestPostUnblockUser(blockedUserId: Int) =
        viewModelScope.launch {
            requestPostUnblockUserUseCase(
                BlockRequestDto(userId = blockedUserId)
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        ApiResponse.data.let { _isSuccessUnblockUser.postValue(Event(true)) }
                    }
                    ApiResult.Status.API_ERROR -> {
                        when (ApiResponse.code) {
                            else -> {
                                _isSuccessUnblockUser.postValue(Event(false))
                            }
                        }
                    }
                    ApiResult.Status.NETWORK_ERROR -> {
                        _isSuccessUnblockUser.postValue(Event(false))
                    }
                }
            }
        }
}