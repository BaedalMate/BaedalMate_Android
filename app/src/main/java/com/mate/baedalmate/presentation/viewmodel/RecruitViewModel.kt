package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.RecruitList
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitMainListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitTagListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitViewModel @Inject constructor(
    private val recruitListUseCase: RequestRecruitListUseCase,
    private val recruitMainListUseCase: RequestRecruitMainListUseCase,
    private val recruitTagListUseCase: RequestRecruitTagListUseCase
) : ViewModel() {
    private val _recruitList = MutableLiveData(RecruitList(emptyList()))
    val recruitList: LiveData<RecruitList> get() = _recruitList

    private val _isRecruitListLoad = MutableLiveData(false)
    val isRecruitListLoad: LiveData<Boolean> get() = _isRecruitListLoad

    private val _recruitHomeRecentList = MutableLiveData(
        MainRecruitList(
            listOf(
                MainRecruitDto(
                    "", 0, "", "", 0,
                    "", 0, "", 0, 0f, ""
                )
            )
        )
    )
    val recruitHomeRecentList: LiveData<MainRecruitList> get() = _recruitHomeRecentList

    private val _recruitHomeRecommendList = MutableLiveData(
        MainRecruitList(
            listOf(
                MainRecruitDto(
                    "", 0, "", "", 0,
                    "", 0, "", 0, 0f, ""
                )
            )
        )
    )
    val recruitHomeRecommendList: LiveData<MainRecruitList> get() = _recruitHomeRecommendList

    private val _isRecruitMainListLoad = MutableLiveData(false)
    val isRecruitMainListLoad: LiveData<Boolean> get() = _isRecruitMainListLoad

    private val _recruitHomeTagList = MutableLiveData(TagRecruitList(emptyList()))
    val recruitHomeTagList: LiveData<TagRecruitList> get() = _recruitHomeTagList

    private val _isRecruitTagListLoad = MutableLiveData(false)
    val isRecruitTagListLoad: LiveData<Boolean> get() = _isRecruitTagListLoad

    fun requestCategoryRecruitList(categoryId: Int, page: Int = 1, size: Int = 4, sort: String) =
        viewModelScope.launch {
            val response = recruitListUseCase.invoke(
                categoryId = categoryId,
                page = page,
                size = size,
                sort = sort
            )
            if (response.isSuccessful) {
                _recruitList.postValue(response.body())
                _isRecruitListLoad.postValue(true)
            } else {
                _isRecruitListLoad.postValue(false)
            }
        }

    fun requestHomeRecruitRecentList(page: Int = 1, size: Int = 4, sort: String) =
        viewModelScope.launch {
            val response = recruitMainListUseCase.invoke(page = page, size = size, sort = sort)
            if (response.isSuccessful) {
                _recruitHomeRecentList.postValue(response.body())
            } else {
            }
        }

    fun requestHomeRecruitRecommendList(page: Int = 1, size: Int = 4, sort: String) =
        viewModelScope.launch {
            val response = recruitMainListUseCase.invoke(page = page, size = size, sort = sort)
            if (response.isSuccessful) {
                _recruitHomeRecommendList.postValue(response.body())
            } else {
            }
        }

    fun requestHomeRecruitTagList(page: Int = 1, size: Int = 4, sort: String) = viewModelScope.launch {
        val response = recruitTagListUseCase.invoke(page = page, size = size, sort = sort)
        if (response.isSuccessful) {
            _recruitHomeTagList.postValue(response.body())
            _isRecruitTagListLoad.postValue(true)
        } else {
            _isRecruitTagListLoad.postValue(false)
        }
    }
}