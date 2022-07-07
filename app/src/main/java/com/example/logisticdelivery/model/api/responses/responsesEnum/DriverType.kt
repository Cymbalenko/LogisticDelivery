package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class DriverType(val value: String) {
    @SerializedName("0")
    Walking("0"),
    @SerializedName("1")
    Moto("1"),
    @SerializedName("2")
    Auto("2")
}