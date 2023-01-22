package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.BuildConfig
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.common.ListLiveData
import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitRequest
import com.mate.baedalmate.domain.model.DeliveryPlatform
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.domain.model.ShippingFeeDto
import com.mate.baedalmate.domain.model.TagDto
import com.mate.baedalmate.data.datasource.remote.write.PlaceMeta
import com.mate.baedalmate.data.datasource.remote.write.RegionInfo
import com.mate.baedalmate.data.datasource.remote.write.ResultSearchKeyword
import com.mate.baedalmate.data.datasource.remote.write.UpdateRecruitDto
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.usecase.write.RequestKakaoLocalUseCase
import com.mate.baedalmate.domain.usecase.write.RequestModifyPostUseCase
import com.mate.baedalmate.domain.usecase.write.RequestUploadPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val kakaoLocalUseCase: RequestKakaoLocalUseCase,
    private val uploadPostUseCase: RequestUploadPostUseCase,
    private val modifyPostUseCase: RequestModifyPostUseCase
) : ViewModel() {
    var categoryId = 0

    var deadLinePeopleCount = 0
    var deadLineAmount = 0
    var deadLineTime: MutableLiveData<String>? = MutableLiveData(null)
    var deadLineCriterion = RecruitFinishCriteria.NUMBER
    var isDeliveryFeeFree = false
    var deliveryFeeRangeList = mutableListOf<ShippingFeeDto>()

    var deliveryDormitory = Dormitory.NURI
    var deliveryStore = MutableLiveData(PlaceDto("", "", "", 0f, 0f))
    var deliveryPlatform = DeliveryPlatform.BAEMIN
    var isCouponUse = false
    var couponAmount = 0

    var postTitle = ""
    var postDetail = ""
    var postTagList = mutableListOf<TagDto>()

    var menuList = ListLiveData<MenuDto>()

    private val _searchResultList = MutableLiveData(
        ResultSearchKeyword(
            PlaceMeta(0, 0, false, RegionInfo(emptyList(), "", "")),
            emptyList()
        )
    )
    val searchResultList: LiveData<ResultSearchKeyword> get() = _searchResultList

    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId: LiveData<Event<Int>> get() = _writePostId

    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess: LiveData<Event<Boolean>> get() = _writeSuccess

    private val _writeModifySuccess = MutableLiveData<Event<Boolean>>()
    val writeModifySuccess: LiveData<Event<Boolean>> get() = _writeModifySuccess

    private val seoulTechEntryPointX = 127.076668
    private val seoulTechEntryPointY = 37.630081

    fun resetVariables() {
        categoryId = 0
        deadLinePeopleCount = 0
        deadLineAmount = 0
        couponAmount = 0
        deadLineTime = MutableLiveData(null)
        deadLineCriterion = RecruitFinishCriteria.NUMBER
        isDeliveryFeeFree = false
        deliveryFeeRangeList = mutableListOf<ShippingFeeDto>()
        deliveryDormitory = Dormitory.NURI
        deliveryStore = MutableLiveData(PlaceDto("", "", "", 0f, 0f))
        isCouponUse = false
        deliveryPlatform = DeliveryPlatform.BAEMIN
        postTitle = ""
        postDetail = ""
        postTagList = mutableListOf<TagDto>()
        menuList = ListLiveData<MenuDto>()
    }

    fun searchPlaceKeyword(
        keyword: String,
        x: String = "$seoulTechEntryPointX",
        y: String = "$seoulTechEntryPointY"
    ) =
        viewModelScope.launch {
            kakaoLocalUseCase.invoke(
                url = "https://dapi.kakao.com/v2/local/search/keyword.json",
                key = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                query = keyword,
                x = x,
                y = y
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        ApiResponse.data.let { _searchResultList.postValue(it) }
                    }
                }
            }
        }

    fun requestUploadPost() = viewModelScope.launch {
        _writeSuccess.postValue(Event(false))

        uploadPostUseCase.invoke(
            body = CreateRecruitRequest(
                categoryId = categoryId,
                coupon = couponAmount,
                criteria = deadLineCriterion,
                deadlineDate =
                if (deadLineTime == null) LocalDateTime.now().plusHours(3L)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) // null 발생으로 인해 오류가 생기는 경우에 대비해 3시간 후로도 설정될 수 있도록 구현
                else deadLineTime?.value!!,
                description = postDetail,
                dormitory = deliveryDormitory,
                freeShipping = isDeliveryFeeFree,
                menu = menuList.value!!.toList(),
                minPeople = deadLinePeopleCount,
                minPrice = deadLineAmount,
                place = deliveryStore.value!!,
                platform = deliveryPlatform,
                shippingFee = deliveryFeeRangeList,
                tags = postTagList,
                title = postTitle
            )
        ).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { ResponseData ->
                        _writePostId.postValue(ResponseData?.let { Event(it.recruitId) })
                    }
                    _writeSuccess.postValue(Event(true))
                }
                else -> {
                    _writeSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestModifyPost(recruitId: Int, categoryId: Int) = viewModelScope.launch {
        _writeModifySuccess.postValue(Event(false))

        modifyPostUseCase.invoke(
            recruitId = recruitId,
            body = UpdateRecruitDto(
                categoryId = categoryId,
                coupon = couponAmount,
                criteria = deadLineCriterion,
                deadlineDate =
                if (deadLineTime == null) LocalDateTime.now().plusHours(3L)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) // null 발생으로 인해 오류가 생기는 경우에 대비해 3시간 후로도 설정될 수 있도록 구현
                else deadLineTime?.value!!,
                description = postDetail,
                dormitory = deliveryDormitory,
                freeShipping = isDeliveryFeeFree,
                menu = menuList.value!!.toList(),
                minPeople = deadLinePeopleCount,
                minPrice = deadLineAmount,
                place = deliveryStore.value!!,
                platform = deliveryPlatform,
                shippingFee = deliveryFeeRangeList,
                tags = postTagList,
                title = postTitle
            )
        ).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    _writeModifySuccess.postValue(Event(true))
                }
                else -> {
                    _writeModifySuccess.postValue(Event(false))
                }
            }
        }
    }
}