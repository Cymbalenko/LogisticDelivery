package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class RestaurantShedulerDays (val value: String) {
    @SerializedName("0")
    Monday("0"),
    @SerializedName("1")
    Tuesday("1"),
    @SerializedName("2")
    Wednesday("2"),
    @SerializedName("3")
    Thursday("3"),
    @SerializedName("4")
    Friday("4"),
    @SerializedName("5")
    Saturday("5"),
    @SerializedName("6")
    Sunday("6")
}