package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import com.mate.baedalmate.domain.usecase.member.RequestLoginKakaoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val requestLoginKakaoUseCase: RequestLoginKakaoUseCase,
    private val tokenPreferencesRepository: TokenPreferencesRepository
) : ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

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
}