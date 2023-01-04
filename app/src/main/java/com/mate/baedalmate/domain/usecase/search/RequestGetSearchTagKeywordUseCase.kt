package com.mate.baedalmate.domain.usecase.search

import com.mate.baedalmate.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetSearchTagKeywordUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(keyword:String, page: Int, size: Int, sort: String) =
        searchRepository.requestGetSearchTagKeyword(
            keyword = keyword,
            page = page,
            size = size,
            sort = sort
        )
}