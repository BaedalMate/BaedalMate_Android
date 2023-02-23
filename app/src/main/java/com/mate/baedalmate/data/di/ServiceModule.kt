package com.mate.baedalmate.data.di

import com.mate.baedalmate.data.datasource.remote.block.BlockApiService
import com.mate.baedalmate.data.datasource.remote.chat.ChatApiService
import com.mate.baedalmate.data.datasource.remote.firebase.FirebaseMessagingService
import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.datasource.remote.notification.NotificationApiService
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitApiService
import com.mate.baedalmate.data.datasource.remote.report.ReportApiService
import com.mate.baedalmate.data.datasource.remote.review.ReviewApiService
import com.mate.baedalmate.data.datasource.remote.search.SearchApiService
import com.mate.baedalmate.data.datasource.remote.write.KakaoLocalApiService
import com.mate.baedalmate.data.datasource.remote.write.WriteApiService
import com.mate.baedalmate.data.repository.BlockRepositoryImpl
import com.mate.baedalmate.data.repository.ChatRepositoryImpl
import com.mate.baedalmate.data.repository.KakaoLocalRepositoryImpl
import com.mate.baedalmate.data.repository.MemberRepositoryImpl
import com.mate.baedalmate.data.repository.NotificationRepositoryImpl
import com.mate.baedalmate.data.repository.RecruitRepositoryImpl
import com.mate.baedalmate.data.repository.ReportRepositoryImpl
import com.mate.baedalmate.data.repository.ReviewRepositoryImpl
import com.mate.baedalmate.data.repository.SearchRepositoryImpl
import com.mate.baedalmate.data.repository.WriteRepositoryImpl
import com.mate.baedalmate.domain.repository.BlockRepository
import com.mate.baedalmate.domain.repository.ChatRepository
import com.mate.baedalmate.domain.repository.KakaoLocalRepository
import com.mate.baedalmate.domain.repository.MemberRepository
import com.mate.baedalmate.domain.repository.NotificationRepository
import com.mate.baedalmate.domain.repository.RecruitRepository
import com.mate.baedalmate.domain.repository.ReportRepository
import com.mate.baedalmate.domain.repository.ReviewRepository
import com.mate.baedalmate.domain.repository.SearchRepository
import com.mate.baedalmate.domain.repository.WriteRepository
import com.mate.baedalmate.domain.usecase.block.RequestGetBlockUserListUseCase
import com.mate.baedalmate.domain.usecase.block.RequestPostBlockUserUseCase
import com.mate.baedalmate.domain.usecase.block.RequestPostUnblockUserUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetAllMenuListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatParticipantsUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomDetailUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetMyMenuListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestPutChangeMyMenuListUseCase
import com.mate.baedalmate.domain.usecase.notification.RegisterFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetHistoryPostCreatedUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetHistoryPostParticipatedUseCase
import com.mate.baedalmate.domain.usecase.member.RequestDeleteResignUserUseCase
import com.mate.baedalmate.domain.usecase.notification.RequestGetFcmTokenUseCase
import com.mate.baedalmate.domain.usecase.member.RequestGetUserInfoUseCase
import com.mate.baedalmate.domain.usecase.member.RequestLogoutUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutChangeMyProfileUseCase
import com.mate.baedalmate.domain.usecase.member.RequestPutUserDormitoryUseCase
import com.mate.baedalmate.domain.usecase.notification.GetNotificationPermitUseCase
import com.mate.baedalmate.domain.usecase.notification.RequestNotificationListUseCase
import com.mate.baedalmate.domain.usecase.notification.SubscribeTopicNoticeUseCase
import com.mate.baedalmate.domain.usecase.notification.UnsubscribeTopicNoticeUseCase
import com.mate.baedalmate.domain.usecase.notification.UpdateNotificationPermitUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCloseRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitMainListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitPostForModifyUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitTagListUseCase
import com.mate.baedalmate.domain.usecase.report.RequestPostReportRecruitUseCase
import com.mate.baedalmate.domain.usecase.report.RequestPostReportUserUseCase
import com.mate.baedalmate.domain.usecase.review.RequestGetTargetReviewUserListUseCase
import com.mate.baedalmate.domain.usecase.review.RequestReviewUsersUseCase
import com.mate.baedalmate.domain.usecase.search.RequestGetSearchTagKeywordUseCase
import com.mate.baedalmate.domain.usecase.write.RequestKakaoLocalUseCase
import com.mate.baedalmate.domain.usecase.write.RequestModifyPostUseCase
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
    fun provideFirebaseMessageService(): FirebaseMessagingService = FirebaseMessagingService()

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
    fun provideGetHistoryPostCreatedUseCase(memberRepository: MemberRepository): RequestGetHistoryPostCreatedUseCase = RequestGetHistoryPostCreatedUseCase(memberRepository)

    @Singleton
    @Provides
    fun provideGetHistoryPostParticipatedUseCase(memberRepository: MemberRepository): RequestGetHistoryPostParticipatedUseCase = RequestGetHistoryPostParticipatedUseCase(memberRepository)

    @Singleton
    @Provides
    fun provideRequestLogoutUseCase(memberRepository: MemberRepository): RequestLogoutUseCase = RequestLogoutUseCase(memberRepository)

    @Singleton
    @Provides
    fun provideDeleteResignUserUseCase(memberRepository: MemberRepository): RequestDeleteResignUserUseCase = RequestDeleteResignUserUseCase(memberRepository)

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
    fun provideModifyPostUseCase(writeRepository: WriteRepository): RequestModifyPostUseCase = RequestModifyPostUseCase(writeRepository)

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
    fun provideRequestRecruitPostForModifyUseCase(recruitRepository: RecruitRepository): RequestRecruitPostForModifyUseCase = RequestRecruitPostForModifyUseCase(recruitRepository)

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

    @Singleton
    @Provides
    fun provideReportApiService(retrofit: Retrofit) = retrofit.create(ReportApiService::class.java)

    @Singleton
    @Provides
    fun provideReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository = reportRepositoryImpl

    @Singleton
    @Provides
    fun provideRequestPostReportUserUseCase(reportRepository: ReportRepository): RequestPostReportUserUseCase = RequestPostReportUserUseCase(reportRepository)

    @Singleton
    @Provides
    fun provideRequestPostReportRecruitUseCase(reportRepository: ReportRepository): RequestPostReportRecruitUseCase = RequestPostReportRecruitUseCase(reportRepository)

    @Singleton
    @Provides
    fun provideBlockApiService(retrofit: Retrofit) = retrofit.create(BlockApiService::class.java)

    @Singleton
    @Provides
    fun provideBlockRepository(blockRepositoryImpl: BlockRepositoryImpl): BlockRepository = blockRepositoryImpl

    @Singleton
    @Provides
    fun providePostBlockUserUseCase(blockRepository: BlockRepository): RequestPostBlockUserUseCase = RequestPostBlockUserUseCase(blockRepository)

    @Singleton
    @Provides
    fun providePostUnblockUserUseCase(blockRepository: BlockRepository): RequestPostUnblockUserUseCase = RequestPostUnblockUserUseCase(blockRepository)

    @Singleton
    @Provides
    fun provideGetBlockedUserListUseCase(blockRepository: BlockRepository): RequestGetBlockUserListUseCase = RequestGetBlockUserListUseCase(blockRepository)

    @Singleton
    @Provides
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService = retrofit.create(NotificationApiService::class.java)

    @Singleton
    @Provides
    fun provideNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl, firebaseMessagingService: FirebaseMessagingService): NotificationRepository = notificationRepositoryImpl

    @Singleton
    @Provides
    fun provideRequestNotifications(notificationRepository: NotificationRepository): RequestNotificationListUseCase = RequestNotificationListUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideRegisterFcmTokenUseCase(notificationRepository: NotificationRepository): RegisterFcmTokenUseCase = RegisterFcmTokenUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideGetFcmTokenUseCase(notificationRepository: NotificationRepository): RequestGetFcmTokenUseCase = RequestGetFcmTokenUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideSubscribeTopicNoticeUseCase(notificationRepository: NotificationRepository): SubscribeTopicNoticeUseCase = SubscribeTopicNoticeUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideUnsubscribeTopicNoticeUseCase(notificationRepository: NotificationRepository): UnsubscribeTopicNoticeUseCase = UnsubscribeTopicNoticeUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideGetNotificationPermitUseCase(notificationRepository: NotificationRepository): GetNotificationPermitUseCase = GetNotificationPermitUseCase(notificationRepository)

    @Singleton
    @Provides
    fun provideUpdateNotificationPermitUseCase(notificationRepository: NotificationRepository): UpdateNotificationPermitUseCase = UpdateNotificationPermitUseCase(notificationRepository)
}