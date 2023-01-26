package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.usecase.search.RequestGetSearchTagKeywordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val requestGetSearchTagKeywordUseCase: RequestGetSearchTagKeywordUseCase
) : ViewModel() {
    private val _searchKeywordResults = MutableStateFlow<PagingData<Pair<RecruitDto, Int>>>(PagingData.empty())
    val searchKeywordResults = _searchKeywordResults.asStateFlow()

    fun requestSearchKeywordResult(
        keyword: String,
        sort: String
    ) = viewModelScope.launch {
        requestGetSearchTagKeywordUseCase(
            keyword = keyword,
            sort = sort
        ).cachedIn(viewModelScope).collectLatest { searchResult ->
            _searchKeywordResults.emit(searchResult)
        }
    }

    fun clearSearchKeywordResult() = viewModelScope.launch {
        _searchKeywordResults.emit(PagingData.empty())
    }
}