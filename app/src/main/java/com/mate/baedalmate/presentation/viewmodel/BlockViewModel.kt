package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.SingleLiveEvent
import com.mate.baedalmate.data.datasource.remote.block.BlockRequestDto
import com.mate.baedalmate.data.datasource.remote.block.BlockedUserListDto
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.usecase.block.RequestGetBlockUserListUseCase
import com.mate.baedalmate.domain.usecase.block.RequestPostBlockUserUseCase
import com.mate.baedalmate.domain.usecase.block.RequestPostUnblockUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    private val requestGetBlockUserListUseCase: RequestGetBlockUserListUseCase,
    private val requestPostBlockUserUseCase: RequestPostBlockUserUseCase,
    private val requestPostUnblockUserUseCase: RequestPostUnblockUserUseCase
) : ViewModel() {
    private val _blockedUserList = MutableLiveData<BlockedUserListDto>()
    val blockedUserList: LiveData<BlockedUserListDto> get() = _blockedUserList

    private val _isSuccessBlockUser = SingleLiveEvent<Boolean>()
    val isSuccessBlockUser: SingleLiveEvent<Boolean> get() = _isSuccessBlockUser
    private val _isAlreadyBlockedUser = SingleLiveEvent<Boolean>()
    val isAlreadyBlockedUser: SingleLiveEvent<Boolean> get() = _isAlreadyBlockedUser

    private val _isSuccessUnblockUser = SingleLiveEvent<Boolean>()
    val isSuccessUnblockUser: SingleLiveEvent<Boolean> get() = _isSuccessUnblockUser

    fun requestGetBlockedUserList() = viewModelScope.launch {
        requestGetBlockUserListUseCase().let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data?.let { userList ->
                        _blockedUserList.postValue(userList)
                    }
                }
            }
        }
    }

    fun requestPostBlockUser(blockUserId: Int) = viewModelScope.launch {
        requestPostBlockUserUseCase(
            BlockRequestDto(userId = blockUserId)
        ).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _isSuccessBlockUser.postValue(true) }
                }
                ApiResult.Status.API_ERROR -> {
                    when (ApiResponse.code) {
                        "400" -> {
                            _isAlreadyBlockedUser.postValue(true)
                        }
                        else -> {
                            _isSuccessBlockUser.postValue(false)
                        }
                    }
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    _isSuccessBlockUser.postValue(false)
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
                        ApiResponse.data.let { _isSuccessUnblockUser.postValue(true) }
                    }
                    ApiResult.Status.API_ERROR -> {
                        when (ApiResponse.code) {
                            else -> {
                                _isSuccessUnblockUser.postValue(false)
                            }
                        }
                    }
                    ApiResult.Status.NETWORK_ERROR -> {
                        _isSuccessUnblockUser.postValue(false)
                    }
                }
            }
        }
}