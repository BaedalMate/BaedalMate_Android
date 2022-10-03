package com.mate.baedalmate.data.di

import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.datasource.remote.write.KakaoLocalApiService
import com.mate.baedalmate.data.repository.KakaoLocalRepositoryImpl
import com.mate.baedalmate.data.repository.MemberRepositoryImpl
import com.mate.baedalmate.domain.repository.KakaoLocalRepository
import com.mate.baedalmate.domain.repository.MemberRepository
import com.mate.baedalmate.domain.usecase.write.RequestKakaoLocalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideMemberApiService(retrofit: Retrofit) = retrofit.create(MemberApiService::class.java)

    @Singleton
    @Provides
    fun provideMemberRepository(memberApiService: MemberApiService): MemberRepository =
        MemberRepositoryImpl(memberApiService)

    @Singleton
    @Provides
    fun provideKakaoLocalApiService(retrofit: Retrofit) = retrofit.create(KakaoLocalApiService::class.java)

    @Singleton
    @Provides
    fun provideKakaoLocalRepository(kakaoLocalRepositoryImpl: KakaoLocalRepositoryImpl): KakaoLocalRepository = kakaoLocalRepositoryImpl

    @Singleton
    @Provides
    fun provideKakaoLocalUseCase(kakaoLocalRepository: KakaoLocalRepository): RequestKakaoLocalUseCase = RequestKakaoLocalUseCase(kakaoLocalRepository)

}