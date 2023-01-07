package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.UpdateUserDto
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import com.mate.baedalmate.domain.usecase.member.RequestLoginKakaoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetUserInfoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutChangeMyProfilePhotoUseCase
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
    private val requestPutChangeMyProfilePhotoUseCase: RequestPutChangeMyProfilePhotoUseCase
) : ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _userInfo = MutableLiveData<UserInfoResponse>()
    val userInfo: LiveData<UserInfoResponse> get() = _userInfo

    private val _isDormitoryChangeSuccess = MutableLiveData<Event<Boolean>>()
    val isDormitoryChangeSuccess: LiveData<Event<Boolean>> get() = _isDormitoryChangeSuccess

    private val _isMyProfileChangeSuccess = MutableLiveData<Event<Boolean>>()
    val isMyProfileChangeSuccess: LiveData<Event<Boolean>> get() = _isMyProfileChangeSuccess

    private val _isMyProfilePhotoChangeSuccess = MutableLiveData<Event<Boolean>>()
    val isMyProfilePhotoChangeSuccess: LiveData<Event<Boolean>> get() = _isMyProfilePhotoChangeSuccess

    fun setKakaoAccessToken(kakaoAccessToken: String) =
        viewModelScope.launch { tokenPreferencesRepository.setKakaoAccessToken(kakaoAccessToken) }

    fun requestLoginKakao(kakaoAccessToken: String) = viewModelScope.launch {
        val response =
            requestLoginKakaoUseCase(MemberOAuthRequest(kakaoAccessToken = kakaoAccessToken))
        if (response.isSuccessful) {
            tokenPreferencesRepository.setOAuthToken(
                MemberOAuthResponse(
                    response.body()!!.accessToken,
                    response.body()!!.refreshToken
                )
            )
            _loginSuccess.postValue(true)
        } else {
            _loginSuccess.postValue(false)
        }
    }

    fun getAccessToken() : String {
        var accessToken = ""
        viewModelScope.launch {
            accessToken = tokenPreferencesRepository.getOAuthToken().accessToken
        }
        return accessToken
    }

    fun requestUserInfo() = viewModelScope.launch {
        val response = requestGetUserInfoUseCase()
        if (response.isSuccessful) {
            _userInfo.postValue(response.body())
        } else {

        }
    }

    fun requestChangeUserDormitory(newDormitory: Dormitory) = viewModelScope.launch {
        val response = requestPutUserDormitoryUseCase(newDormitory = newDormitory)
        if (response.isSuccessful) {
            _isDormitoryChangeSuccess.postValue(Event(true))
        } else {
            _isDormitoryChangeSuccess.postValue(Event(false))
        }
    }

    fun requestPutChangeMyProfile(newNickname: String) = viewModelScope.launch {
        requestPutChangeMyProfileUseCase(updateUserInfo = UpdateUserDto(nickname = newNickname)).let {
            if (it.isSuccessful) {
                _isMyProfileChangeSuccess.postValue(Event(true))
            } else {
                _isMyProfileChangeSuccess.postValue(Event(false))
            }
        }
    }

    fun requestPutChangeMyProfilePhoto(newImageFile: File) = viewModelScope.launch {
        requestPutChangeMyProfilePhotoUseCase(newImageFile = newImageFile)?.let {
            if (it.isSuccessful) {
                _isMyProfilePhotoChangeSuccess.postValue(Event(true))
            } else {
                _isMyProfilePhotoChangeSuccess.postValue(Event(false))
            }
        }
    }
}