package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class RestaurantStatus (val value: String) {
    @SerializedName("0")
    Active("0"),
    @SerializedName("1")
    Inactive("1"),
    @SerializedName("2")
    OnPause("2"),
    @SerializedName("3")
    Сompleted("3")
}