package com.mate.baedalmate.domain.usecase.search

import com.mate.baedalmate.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetSearchTagKeywordUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(keyword: String, sort: String) =
        searchRepository.requestGetSearchTagKeyword(keyword = keyword, sort = sort)
}