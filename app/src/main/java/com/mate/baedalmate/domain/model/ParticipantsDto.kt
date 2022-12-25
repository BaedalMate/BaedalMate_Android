package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class ParticipantsDto(
    @SerializedName("participants")
    val participants: List<ParticipantDto>,
    @SerializedName("recruitId")
    val recruitId: Int
)