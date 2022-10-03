package com.mate.baedalmate.domain.usecase.write

import com.mate.baedalmate.domain.repository.KakaoLocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestKakaoLocalUseCase @Inject constructor(private val kakaoLocalRepository: KakaoLocalRepository) {
    suspend operator fun invoke(url: String, key: String, query: String, x: String, y: String) =
        kakaoLocalRepository.getKakaoLocalSearchKeyword(url = url, key = key, query = query, x = x, y = y)
}