package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.BuildConfig
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
    var deadLineTime = "2022-12-24T16:28:27" // TODO 수정 필요
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

    private val _writePostId = MutableLiveData<Int>()
    val writePostId: LiveData<Int> get() = _writePostId

    private val _writeSuccess = MutableLiveData<Boolean>()
    val writeSuccess: LiveData<Boolean> get() = _writeSuccess

    fun searchPlaceKeyword(keyword: String, x: String = "127.076668", y: String = "37.630081") =
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
        val response = uploadPostUseCase.invoke(
            body = CreateRecruitRequest(
                categoryId = categoryId,
                coupon = couponAmount,
                criteria = deadLineCriterion,
                deadlineDate = deadLineTime,
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
            _writePostId.postValue(response.body()?.id ?: 0)
            _writeSuccess.postValue(true)
        } else {
            _writeSuccess.postValue(false)
        }
    }
}