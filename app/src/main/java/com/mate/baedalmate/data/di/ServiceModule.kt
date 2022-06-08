package com.mate.baedalmate.data.di

import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.repository.MemberRepositoryImpl
import com.mate.baedalmate.domain.repository.MemberRepository
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
}