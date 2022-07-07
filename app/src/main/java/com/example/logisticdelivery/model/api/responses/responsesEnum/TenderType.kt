package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class TenderType (val value: String) {
    @SerializedName("0")
    Cash("0"),
    @SerializedName("1")
    Payment("1"),
    @SerializedName("2")
    Terminal("2")
}