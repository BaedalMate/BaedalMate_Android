package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.domain.usecase.member.RequestLoginKakaoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val requestLoginKakaoUseCase: RequestLoginKakaoUseCase
) : ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    fun requestLoginKakao(accessToken: String) = viewModelScope.launch {
        val response = requestLoginKakaoUseCase(MemberOAuthRequest(kakao_access_token = accessToken))
        if (response.isSuccessful) {
            _loginSuccess.postValue(true)
        } else {
            _loginSuccess.postValue(false)
        }
    }
}