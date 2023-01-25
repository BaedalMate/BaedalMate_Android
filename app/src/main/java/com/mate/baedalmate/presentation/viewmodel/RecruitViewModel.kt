package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitDetailForModify
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCloseRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitMainListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitPostForModifyUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitTagListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitViewModel @Inject constructor(
    private val recruitListUseCase: RequestRecruitListUseCase,
    private val recruitMainListUseCase: RequestRecruitMainListUseCase,
    private val recruitTagListUseCase: RequestRecruitTagListUseCase,
    private val recruitPostUseCase: RequestRecruitPostUseCase,
    private val recruitPostForModifyUseCase: RequestRecruitPostForModifyUseCase,
    private val closeRecruitPostUseCase: RequestCloseRecruitPostUseCase,
    private val cancelRecruitPostUseCase: RequestCancelRecruitPostUseCase,
    private val participateRecruitPostUseCase: RequestParticipateRecruitPostUseCase,
    private val cancelParticipateRecruitPostUseCase: RequestCancelParticipateRecruitPostUseCase
) : ViewModel() {
    private val _recruitListAll = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListAll = _recruitListAll.asStateFlow()
    private val _recruitListKorean = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListKorean = _recruitListKorean.asStateFlow()
    private val _recruitListChinese = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListChinese = _recruitListChinese.asStateFlow()
    private val _recruitListJapanese = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListJapanese = _recruitListJapanese.asStateFlow()
    private val _recruitListWestern = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListWestern = _recruitListWestern.asStateFlow()
    private val _recruitListFastfood = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListFastfood = _recruitListFastfood.asStateFlow()
    private val _recruitListBunsik = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListBunsik = _recruitListBunsik.asStateFlow()
    private val _recruitListDessert = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListDessert = _recruitListDessert.asStateFlow()
    private val _recruitListChicken = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListChicken = _recruitListChicken.asStateFlow()
    private val _recruitListPizza = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListPizza = _recruitListPizza.asStateFlow()
    private val _recruitListAsia = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListAsia = _recruitListAsia.asStateFlow()
    private val _recruitListPackedmeal = MutableStateFlow<PagingData<RecruitDto>>(PagingData.empty())
    val recruitListPackedmeal = _recruitListPackedmeal.asStateFlow()

    private val _isRecruitListLoad = MutableLiveData(false)
    val isRecruitListLoad: LiveData<Boolean> get() = _isRecruitListLoad

    private val _isCancelRecruitPostSuccess = MutableLiveData<Event<Boolean>>()
    val isCancelRecruitPostSuccess: LiveData<Event<Boolean>> get() = _isCancelRecruitPostSuccess

    private val _isCloseRecruitPostSuccess = MutableLiveData<Event<Boolean>>()
    val isCloseRecruitPostSuccess: LiveData<Event<Boolean>> get() = _isCloseRecruitPostSuccess

    private val _isParticipateRecruitPostSuccess = MutableLiveData<Event<Boolean>>()
    val isParticipateRecruitPostSuccess: LiveData<Event<Boolean>> get() = _isParticipateRecruitPostSuccess

    private val _isCancelParticipateRecruitPostSuccess = MutableLiveData<Event<Boolean>>()
    val isCancelParticipateRecruitPostSuccess: LiveData<Event<Boolean>> get() = _isCancelParticipateRecruitPostSuccess

    private val _recruitPostParticipateInfo = MutableLiveData<CreateOrderResponse>()
    val recruitPostParticipateInfo: LiveData<CreateOrderResponse> get() = _recruitPostParticipateInfo

    private val _recruitPostDetail = MutableLiveData(
        RecruitDetail(
            false,
            0,
            0,
            "",
            "",
            "",
            false,
            "",
            0,
            false,
            PlaceDto("", "", "", 0f, 0f),
            "",
            0,
            0f,
            0,
            emptyList(),
            "",
            UserInfoResponse("", "", "", 5f, 0L),
            0,
            0,
            false
        )
    )
    val recruitPostDetail: LiveData<RecruitDetail> get() = _recruitPostDetail

    private val _recruitPostDetailForModify = MutableLiveData<RecruitDetailForModify>()
    val recruitPostDetailForModify get() = _recruitPostDetailForModify

    private val _recruitHomeRecentList = MutableLiveData(
        MainRecruitList(
            listOf(
                MainRecruitDto(
                    "", 0, "", "", 0,
                    "", 0, 0, "", 0, 0f, "", true
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
                    "", 0, 0, "", 0, 0f, "", true
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

    fun requestCategoryRecruitList(
        categoryId: Int?,
        exceptClose: Boolean= false,
        sort: String
    ) = viewModelScope.launch {
        recruitListUseCase(categoryId, exceptClose, sort).cachedIn(viewModelScope)
            .collectLatest { recruitList ->
                when (categoryId) {
                    null -> _recruitListAll.emit(recruitList)
                    1 -> _recruitListKorean.emit(recruitList)
                    2 -> _recruitListChinese.emit(recruitList)
                    3 -> _recruitListJapanese.emit(recruitList)
                    4 -> _recruitListWestern.emit(recruitList)
                    5 -> _recruitListFastfood.emit(recruitList)
                    6 -> _recruitListBunsik.emit(recruitList)
                    7 -> _recruitListDessert.emit(recruitList)
                    8 -> _recruitListChicken.emit(recruitList)
                    9 -> _recruitListPizza.emit(recruitList)
                    10 -> _recruitListAsia.emit(recruitList)
                    11 -> _recruitListPackedmeal.emit(recruitList)
                    else -> _recruitListKorean.emit(recruitList)
                }
                _isRecruitListLoad.postValue(true)
            }
    }

    fun requestHomeRecruitRecentList(page: Int = 0, size: Int = 4, sort: String) =
        viewModelScope.launch {
            recruitMainListUseCase.invoke(page = page, size = size, sort = sort)
                .let { ApiResponse ->
                    when (ApiResponse.status) {
                        ApiResult.Status.SUCCESS -> {
                            _recruitHomeRecentList.postValue(ApiResponse.data)
                        }
                    }
                }
        }

    fun requestHomeRecruitRecommendList(page: Int = 0, size: Int = 4, sort: String) =
        viewModelScope.launch {
            recruitMainListUseCase.invoke(page = page, size = size, sort = sort)
                .let { ApiResponse ->
                    when (ApiResponse.status) {
                        ApiResult.Status.SUCCESS -> {
                            _recruitHomeRecommendList.postValue(ApiResponse.data)
                        }
                    }
                }
        }

    fun requestHomeRecruitTagList(page: Int = 0, size: Int = 4, sort: String) =
        viewModelScope.launch {
            recruitTagListUseCase.invoke(page = page, size = size, sort = sort).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        ApiResponse.data.let { _recruitHomeTagList.postValue(it) }
                        _isRecruitTagListLoad.postValue(true)
                    }
                    else -> {
                        _isRecruitTagListLoad.postValue(false)
                    }
                }
            }
        }

    fun requestRecruitPost(postId: Int) = viewModelScope.launch {
        recruitPostUseCase.invoke(id = postId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _recruitPostDetail.postValue(it) }
                }
            }
        }
    }

    fun requestRecruitPostForModify(postId: Int) = viewModelScope.launch {
        recruitPostForModifyUseCase.invoke(id = postId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _recruitPostDetailForModify.postValue(it) }
                }
            }
        }
    }

    fun requestCancelRecruitPost(postId: Int) = viewModelScope.launch {
        cancelRecruitPostUseCase.invoke(id = postId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isCancelRecruitPostSuccess.postValue(Event(true))
                }
                else -> {
                    _isCancelRecruitPostSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestCloseRecruitPost(postId: Int) = viewModelScope.launch {
        closeRecruitPostUseCase.invoke(id = postId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _isCloseRecruitPostSuccess.postValue(Event(true))
                }
                else -> {
                    _isCloseRecruitPostSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestParticipateRecruitPost(menuList: List<MenuDto>, roomId: Int) =
        viewModelScope.launch {
            participateRecruitPostUseCase.invoke(
                data = CreateOrderRequest(
                    menu = menuList, recruitId = roomId
                )
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        _isParticipateRecruitPostSuccess.postValue(Event(true))
                        ApiResponse.data.let { _recruitPostParticipateInfo.postValue(it) }
                    }
                    else -> {
                        _isParticipateRecruitPostSuccess.postValue(Event(false))
                    }
                }
            }
        }

    fun requestCancelParticipateRecruitPost(postId: Int) =
        viewModelScope.launch {
            cancelParticipateRecruitPostUseCase.invoke(data = DeleteOrderDto(recruitId = postId))
                .let { ApiResponse ->
                    when (ApiResponse.status) {
                        ApiResult.Status.SUCCESS -> {
                            _isCancelParticipateRecruitPostSuccess.postValue(Event(true))
                        }
                        else -> {
                            _isCancelParticipateRecruitPostSuccess.postValue(Event(false))
                        }
                    }
                }
        }
}