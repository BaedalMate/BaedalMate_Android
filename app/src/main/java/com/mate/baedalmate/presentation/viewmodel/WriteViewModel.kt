package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitFinishCriteria
import com.mate.baedalmate.data.datasource.remote.recruit.ShippingFeeDto
import dagger.hilt.android.lifecycle.HiltViewModel

class WriteViewModel : ViewModel() {
    var categoryId = 0

    var deadLinePeopleCount = 0
    var deadLineAmount = 0
    var deadLineTime = "2020-12-24T16:28:27" // TODO 수정 필요
    var deadLineCriterion = RecruitFinishCriteria.NUMBER
    var isDeliveryFeeFree = false
    var deliveryFeeRangeList = mutableListOf<ShippingFeeDto>()
}