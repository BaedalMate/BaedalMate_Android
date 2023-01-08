package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitList
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCancelRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestCloseRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestParticipateRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitMainListUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitPostUseCase
import com.mate.baedalmate.domain.usecase.recruit.RequestRecruitTagListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitViewModel @Inject constructor(
    private val recruitListUseCase: RequestRecruitListUseCase,
    private val recruitMainListUseCase: RequestRecruitMainListUseCase,
    private val recruitTagListUseCase: RequestRecruitTagListUseCase,
    private val recruitPostUseCase: RequestRecruitPostUseCase,
    private val closeRecruitPostUseCase: RequestCloseRecruitPostUseCase,
    private val cancelRecruitPostUseCase: RequestCancelRecruitPostUseCase,
    private val participateRecruitPostUseCase: RequestParticipateRecruitPostUseCase,
    private val cancelParticipateRecruitPostUseCase: RequestCancelParticipateRecruitPostUseCase
) : ViewModel() {
    private val _recruitListAll = MutableLiveData(RecruitList(emptyList()))
    val recruitListAll: LiveData<RecruitList> get() = _recruitListAll
    private val _recruitListKorean = MutableLiveData(RecruitList(emptyList()))
    val recruitListKorean: LiveData<RecruitList> get() = _recruitListKorean
    private val _recruitListChinese = MutableLiveData(RecruitList(emptyList()))
    val recruitListChinese: LiveData<RecruitList> get() = _recruitListChinese
    private val _recruitListJapanese = MutableLiveData(RecruitList(emptyList()))
    val recruitListJapanese: LiveData<RecruitList> get() = _recruitListJapanese
    private val _recruitListWestern = MutableLiveData(RecruitList(emptyList()))
    val recruitListWestern: LiveData<RecruitList> get() = _recruitListWestern
    private val _recruitListFastfood = MutableLiveData(RecruitList(emptyList()))
    val recruitListFastfood: LiveData<RecruitList> get() = _recruitListFastfood
    private val _recruitListBunsik = MutableLiveData(RecruitList(emptyList()))
    val recruitListBunsik: LiveData<RecruitList> get() = _recruitListBunsik
    private val _recruitListDessert = MutableLiveData(RecruitList(emptyList()))
    val recruitListDessert: LiveData<RecruitList> get() = _recruitListDessert
    private val _recruitListChicken = MutableLiveData(RecruitList(emptyList()))
    val recruitListChicken: LiveData<RecruitList> get() = _recruitListChicken
    private val _recruitListPizza = MutableLiveData(RecruitList(emptyList()))
    val recruitListPizza: LiveData<RecruitList> get() = _recruitListPizza
    private val _recruitListAsia = MutableLiveData(RecruitList(emptyList()))
    val recruitListAsia: LiveData<RecruitList> get() = _recruitListAsia
    private val _recruitListPackedmeal = MutableLiveData(RecruitList(emptyList()))
    val recruitListPackedmeal: LiveData<RecruitList> get() = _recruitListPackedmeal

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
        categoryId: Int? = null,
        page: Int = 0,
        size: Int = 4,
        sort: String
    ) =
        viewModelScope.launch {
            recruitListUseCase.invoke(
                categoryId = categoryId,
                page = page,
                size = size,
                sort = sort
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        ApiResponse.data.let { recruitList ->
                            when (categoryId) {
                                null -> _recruitListAll.postValue(recruitList)
                                1 -> _recruitListKorean.postValue(recruitList)
                                2 -> _recruitListChinese.postValue(recruitList)
                                3 -> _recruitListJapanese.postValue(recruitList)
                                4 -> _recruitListWestern.postValue(recruitList)
                                5 -> _recruitListFastfood.postValue(recruitList)
                                6 -> _recruitListBunsik.postValue(recruitList)
                                7 -> _recruitListDessert.postValue(recruitList)
                                8 -> _recruitListChicken.postValue(recruitList)
                                9 -> _recruitListPizza.postValue(recruitList)
                                10 -> _recruitListAsia.postValue(recruitList)
                                11 -> _recruitListPackedmeal.postValue(recruitList)
                                else -> _recruitListAll.postValue(recruitList)
                            }

                        }
                        _isRecruitListLoad.postValue(true)
                    }
                    else -> {
                        _isRecruitListLoad.postValue(false)

                    }
                }
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