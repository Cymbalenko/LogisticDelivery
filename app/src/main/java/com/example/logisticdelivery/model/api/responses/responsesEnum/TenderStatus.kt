package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class TenderStatus (val value: String) {
    @SerializedName("0")
    Paid("0"),
    @SerializedName("1")
    NotPaid("1")
}