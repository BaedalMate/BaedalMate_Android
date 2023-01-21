package com.mate.baedalmate.presentation.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.BaedalMateApplication
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitList
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.UpdateUserDto
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import com.mate.baedalmate.domain.usecase.member.RequestGetHistoryPostCreatedUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetHistoryPostParticipatedUseCase
import com.mate.baedalmate.domain.usecase.member.RequestDeleteResignUserUseCase
import com.mate.baedalmate.domain.usecase.member.RequestLoginKakaoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetUserInfoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestLogoutUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutChangeMyProfileUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutUserDormitoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val requestLoginKakaoUseCase: RequestLoginKakaoUseCase,
    private val requestGetUserInfoUseCase: RequestGetUserInfoUseCase,
    private val requestPutUserDormitoryUseCase: RequestPutUserDormitoryUseCase,
    private val tokenPreferencesRepository: TokenPreferencesRepository,
    private val requestPutChangeMyProfileUseCase: RequestPutChangeMyProfileUseCase,
    private val requestGetHistoryPostCreatedUseCase: RequestGetHistoryPostCreatedUseCase,
    private val requestGetHistoryPostParticipatedUseCase: RequestGetHistoryPostParticipatedUseCase,
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestDeleteResignUserUseCase: RequestDeleteResignUserUseCase
) : ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    val isAccountValid: LiveData<Boolean?> get() = tokenPreferencesRepository.isAccountValid.asLiveData()

    private val _userInfo = MutableLiveData<UserInfoResponse>()
    val userInfo: LiveData<UserInfoResponse> get() = _userInfo

    private val _getUserInfoSuccess = MutableLiveData<Boolean>()
    val getUserInfoSuccess get() = _getUserInfoSuccess

    private val _isDormitoryChangeSuccess = MutableLiveData<Event<Boolean>>()
    val isDormitoryChangeSuccess: LiveData<Event<Boolean>> get() = _isDormitoryChangeSuccess

    private val _isMyProfileChangeSuccess = MutableLiveData<Event<Boolean>>()
    val isMyProfileChangeSuccess: LiveData<Event<Boolean>> get() = _isMyProfileChangeSuccess

    private val _historyPostCreatedList = MutableLiveData(HistoryRecruitList(emptyList()))
    val historyPostCreatedList: LiveData<HistoryRecruitList> get() = _historyPostCreatedList

    private val _historyPostParticipatedList = MutableLiveData(HistoryRecruitList(emptyList()))
    val historyPostParticipatedList: LiveData<HistoryRecruitList> get() = _historyPostParticipatedList

    private val _isLogoutSuccess = MutableLiveData<Event<Boolean>>()
    val isLogoutSuccess: LiveData<Event<Boolean>> get() = _isLogoutSuccess

    private val _isResignSuccess = MutableLiveData<Event<Boolean>>()
    val isResignSuccess: LiveData<Event<Boolean>> get() = _isResignSuccess

    fun setKakaoAccessToken(kakaoAccessToken: String) =
        viewModelScope.launch { tokenPreferencesRepository.setKakaoAccessToken(kakaoAccessToken) }

    fun requestLoginKakao(kakaoAccessToken: String) = viewModelScope.launch {
        requestLoginKakaoUseCase(MemberOAuthRequest(kakaoAccessToken = kakaoAccessToken)).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data?.let {
                        tokenPreferencesRepository.setOAuthToken(
                            MemberOAuthResponse(
                                it.accessToken,
                                it.refreshToken
                            )
                        )
                    }
                    _loginSuccess.postValue(true)
                }
                else -> {
                    _loginSuccess.postValue(false)
                }
            }
        }
    }

    fun getAccessToken(): String {
        var accessToken = ""
        viewModelScope.launch {
            accessToken = tokenPreferencesRepository.getOAuthToken().accessToken
        }
        return accessToken
    }

    suspend fun getUserInfo(): UserInfoResponse? {
        return tokenPreferencesRepository.getUserInfo()
    }

    fun requestUserInfo() = viewModelScope.launch {
        requestGetUserInfoUseCase()?.let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _getUserInfoSuccess.postValue(true)
                    ApiResponse.data?.let {
                        _userInfo.postValue(it)
                        tokenPreferencesRepository.setUserInfo(it)
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    _getUserInfoSuccess.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestChangeUserDormitory(newDormitory: Dormitory) = viewModelScope.launch {
        requestPutUserDormitoryUseCase(newDormitory = newDormitory).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isDormitoryChangeSuccess.postValue(Event(true))
                }
                else -> {
                    _isDormitoryChangeSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestPutChangeMyProfile(isChangingDefaultImage: Boolean = false, newNickname: String, newImageFile: File?) = viewModelScope.launch {
        requestPutChangeMyProfileUseCase(isChangingDefaultImage = isChangingDefaultImage, newNickname = newNickname, newImageFile = newImageFile).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isMyProfileChangeSuccess.postValue(Event(true))
                }
                else -> {
                    _isMyProfileChangeSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestGetHistoryPostCreatedList(page: Int, size: Int, sort: String) = viewModelScope.launch {
        requestGetHistoryPostCreatedUseCase.invoke(page = page, size = size, sort = sort).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _historyPostCreatedList.postValue(ApiResponse.data)
                }
            }
        }
    }

    fun requestGetHistoryPostParticipatedList(page: Int, size: Int, sort: String) = viewModelScope.launch {
        requestGetHistoryPostParticipatedUseCase.invoke(page = page, size = size, sort = sort).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _historyPostParticipatedList.postValue(ApiResponse.data)
                }
            }
        }
    }
    
    fun requestClearAllLocalData() = viewModelScope.launch {
        tokenPreferencesRepository.clearAllInfo()
    }

    fun requestResign() = viewModelScope.launch {
        requestDeleteResignUserUseCase().let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isResignSuccess.postValue(Event(true))
                }
                ApiResult.Status.API_ERROR -> {
                    when (ApiResponse.code) {
                        "400" -> Toast.makeText(
                            BaedalMateApplication.applicationContext(),
                            "참여중인 모집글이 있어 탈퇴처리가 완료되지 않았습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        else -> _isResignSuccess.postValue(Event(false))
                    }
                }
                else -> {
                    _isResignSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestLogout() = viewModelScope.launch {
        requestLogoutUseCase().let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isLogoutSuccess.postValue(Event(true))
                }
                else -> {
                    _isLogoutSuccess.postValue(Event(false))
                }
            }
        }
    }
}