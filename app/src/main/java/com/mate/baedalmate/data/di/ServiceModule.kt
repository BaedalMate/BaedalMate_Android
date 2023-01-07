package com.mate.baedalmate.data.di

import com.mate.baedalmate.data.datasource.remote.chat.ChatApiService
import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitApiService
import com.mate.baedalmate.data.datasource.remote.review.ReviewApiService
import com.mate.baedalmate.data.datasource.remote.search.SearchApiService
import com.mate.baedalmate.data.datasource.remote.write.KakaoLocalApiService
import com.mate.baedalmate.data.datasource.remote.write.WriteApiService
import com.mate.baedalmate.data.repository.ChatRepositoryImpl
import com.mate.baedalmate.data.repository.KakaoLocalRepositoryImpl
import com.mate.baedalmate.data.repository.MemberRepositoryImpl
import com.mate.baedalmate.data.repository.RecruitRepositoryImpl
import com.mate.baedalmate.data.repository.ReviewRepositoryImpl
import com.mate.baedalmate.data.repository.SearchRepositoryImpl
import com.mate.baedalmate.data.repository.WriteRepositoryImpl
import com.mate.baedalmate.domain.repository.ChatRepository
import com.mate.baedalmate.domain.repository.KakaoLocalRepository
import com.mate.baedalmate.domain.repository.MemberRepository
import com.mate.baedalmate.domain.repository.RecruitRepository
import com.mate.baedalmate.domain.repository.ReviewRepository
import com.mate.baedalmate.domain.repository.SearchRepository
import com.mate.baedalmate.domain.repository.WriteRepository
import com.mate.baedalmate.domain.usecase.chat.RequestGetAllMenuListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatParticipantsUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomDetailUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetMyMenuListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestPutChangeMyMenuListUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetUserInfoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutChangeMyProfilePhotoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutChangeMyProfileUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutUserDormitoryUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCloseRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitMainListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitTagListUseCase
import com.mate.baedalmate.domain.usecase.review.RequestGetTargetReviewUserListUseCase
import com.mate.baedalmate.domain.usecase.review.RequestReviewUsersUseCase
import com.mate.baedalmate.domain.usecase.search.RequestGetSearchTagKeywordUseCase
import com.mate.baedalmate.domain.usecase.write.RequestKakaoLocalUseCase
import com.mate.baedalmate.domain.usecase.write.RequestUploadPostUseCase
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
    fun provideGetUserInfoUseCase(memberRepository: MemberRepository): RequestGetUserInfoUseCase = RequestGetUserInfoUseCase(memberRepository)

    @Singleton
    @Provides
    fun providePutUserDormitoryUseCase(memberRepository: MemberRepository): RequestPutUserDormitoryUseCase = RequestPutUserDormitoryUseCase(memberRepository)

    @Singleton
    @Provides
    fun providePutChangeMyProfileUseCase(memberRepository: MemberRepository): RequestPutChangeMyProfileUseCase = RequestPutChangeMyProfileUseCase(memberRepository)

    @Singleton
    @Provides
    fun providePutChangeMyProfilePhotoUseCase(memberRepository: MemberRepository): RequestPutChangeMyProfilePhotoUseCase = RequestPutChangeMyProfilePhotoUseCase(memberRepository)

    @Singleton
    @Provides
    fun provideKakaoLocalApiService(retrofit: Retrofit) = retrofit.create(KakaoLocalApiService::class.java)

    @Singleton
    @Provides
    fun provideKakaoLocalRepository(kakaoLocalRepositoryImpl: KakaoLocalRepositoryImpl): KakaoLocalRepository = kakaoLocalRepositoryImpl

    @Singleton
    @Provides
    fun provideKakaoLocalUseCase(kakaoLocalRepository: KakaoLocalRepository): RequestKakaoLocalUseCase = RequestKakaoLocalUseCase(kakaoLocalRepository)

    @Singleton
    @Provides
    fun provideWriteApiService(retrofit: Retrofit) = retrofit.create(WriteApiService::class.java)

    @Singleton
    @Provides
    fun provideWriteRepository(writeRepositoryImpl: WriteRepositoryImpl): WriteRepository = writeRepositoryImpl

    @Singleton
    @Provides
    fun provideUploadPostUseCase(writeRepository: WriteRepository): RequestUploadPostUseCase = RequestUploadPostUseCase(writeRepository)

    @Singleton
    @Provides
    fun provideRecruitApiService(retrofit: Retrofit) = retrofit.create(RecruitApiService::class.java)

    @Singleton
    @Provides
    fun provideRecruitRepository(recruitRepositoryImpl: RecruitRepositoryImpl): RecruitRepository = recruitRepositoryImpl

    @Singleton
    @Provides
    fun provideRecruitListUseCase(recruitRepository: RecruitRepository): RequestRecruitListUseCase = RequestRecruitListUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRecruitMainListUseCase(recruitRepository: RecruitRepository): RequestRecruitMainListUseCase = RequestRecruitMainListUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRecruitTagListUseCase(recruitRepository: RecruitRepository): RequestRecruitTagListUseCase = RequestRecruitTagListUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRequestRecruitPostUseCase(recruitRepository: RecruitRepository): RequestRecruitPostUseCase = RequestRecruitPostUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRequestCancelRecruitPostUseCase(recruitRepository: RecruitRepository): RequestCancelRecruitPostUseCase = RequestCancelRecruitPostUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRequestCloseRecruitPostUseCase(recruitRepository: RecruitRepository): RequestCloseRecruitPostUseCase = RequestCloseRecruitPostUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRequestParticipateRecruitPostUseCase(recruitRepository: RecruitRepository): RequestParticipateRecruitPostUseCase = RequestParticipateRecruitPostUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideRequestCancelParticipateRecruitPostUseCase(recruitRepository: RecruitRepository): RequestCancelParticipateRecruitPostUseCase = RequestCancelParticipateRecruitPostUseCase(recruitRepository)

    @Singleton
    @Provides
    fun provideChatApiService(retrofit: Retrofit) = retrofit.create(ChatApiService::class.java)

    @Singleton
    @Provides
    fun provideChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository = chatRepositoryImpl

    @Singleton
    @Provides
    fun provideRequestGetChatRoomListUseCase(chatRepository: ChatRepository): RequestGetChatRoomListUseCase = RequestGetChatRoomListUseCase(chatRepository)

    @Singleton
    @Provides
    fun provideRequestGetChatRoomDetailUseCase(chatRepository: ChatRepository): RequestGetChatRoomDetailUseCase = RequestGetChatRoomDetailUseCase(chatRepository)

    @Singleton
    @Provides
    fun provideRequestGetChatParticipantsUseCase(chatRepository: ChatRepository): RequestGetChatParticipantsUseCase = RequestGetChatParticipantsUseCase(chatRepository)

    @Singleton
    @Provides
    fun provideRequestGetAllMenuListUseCase(chatRepository: ChatRepository): RequestGetAllMenuListUseCase = RequestGetAllMenuListUseCase(chatRepository)

    @Singleton
    @Provides
    fun provideRequestGetMyMenuListUseCase(chatRepository: ChatRepository): RequestGetMyMenuListUseCase = RequestGetMyMenuListUseCase(chatRepository)

    @Singleton
    @Provides
    fun provideRequestPutChangeMyMenuListUseCase(chatRepository: ChatRepository): RequestPutChangeMyMenuListUseCase = RequestPutChangeMyMenuListUseCase(chatRepository)

    @Singleton
    @Provides
    fun provideReviewApiService(retrofit: Retrofit) = retrofit.create(ReviewApiService::class.java)

    @Singleton
    @Provides
    fun provideReviewRepository(reviewApiService: ReviewApiService): ReviewRepository = ReviewRepositoryImpl(reviewApiService)

    @Singleton
    @Provides
    fun provideGetTargetReviewUserListUseCase(reviewRepository: ReviewRepository): RequestGetTargetReviewUserListUseCase = RequestGetTargetReviewUserListUseCase(reviewRepository)

    @Singleton
    @Provides
    fun provideReviewUsersUseCase(reviewRepository: ReviewRepository): RequestReviewUsersUseCase = RequestReviewUsersUseCase(reviewRepository)

    @Singleton
    @Provides
    fun provideSearchApiService(retrofit: Retrofit) = retrofit.create(SearchApiService::class.java)

    @Singleton
    @Provides
    fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository = searchRepositoryImpl

    @Singleton
    @Provides
    fun provideRequestGetSearchTagKeywordUseCase(searchRepository: SearchRepository): RequestGetSearchTagKeywordUseCase = RequestGetSearchTagKeywordUseCase(searchRepository)

}