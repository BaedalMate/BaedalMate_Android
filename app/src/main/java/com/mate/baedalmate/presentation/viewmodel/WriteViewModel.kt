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
import com.mate.baedalmate.domain.usecase.write.RequestKakaoLocalUseCase
import com.mate.baedalmate.domain.usecase.write.RequestUploadPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val kakaoLocalUseCase: RequestKakaoLocalUseCase,
    private val uploadPostUseCase: RequestUploadPostUseCase
) :
    ViewModel() {
    var categoryId = 0

    var deadLinePeopleCount = 0
    var deadLineAmount = 0
    var deadLineTime = "2022-12-31T00:00:00" // TODO 사용자 입력이 가능해지면 사용될 변수
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

    private val seoulTechEntryPointX = 127.076668
    private val seoulTechEntryPointY = 37.630081

    fun resetVariables() {
        categoryId = 0
        deadLinePeopleCount = 0
        deadLineAmount = 0
        couponAmount = 0
        deadLineTime = "2022-12-31T00:00:00"
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
            val response = kakaoLocalUseCase.invoke(
                url = "https://dapi.kakao.com/v2/local/search/keyword.json",
                key = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                query = keyword,
                x = x,
                y = y
            )
            if (response.isSuccessful) {
                _searchResultList.postValue(response.body()!!)
            }
        }

    fun requestUploadPost() = viewModelScope.launch {
        _writeSuccess.postValue(Event(false))

        val response = uploadPostUseCase.invoke(
            body = CreateRecruitRequest(
                categoryId = categoryId,
                coupon = couponAmount,
                criteria = deadLineCriterion,
                deadlineDate = LocalDateTime.now().plusHours(3L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
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
        )

        if (response.isSuccessful) {
            _writePostId.postValue(response.body()?.let { Event(it.id) })
            _writeSuccess.postValue(Event(true))
        } else {
            _writeSuccess.postValue(Event(false))
        }
    }
}