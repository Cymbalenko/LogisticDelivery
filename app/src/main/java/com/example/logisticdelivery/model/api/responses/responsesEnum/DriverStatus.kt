package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class DriverStatus (val value: String) {
    @SerializedName("0")
    InWork("0"),
    @SerializedName("1")
    Pause("1"),
    @SerializedName("2")
    Problem("2"),
    @SerializedName("3")
    NotActive("3"),
    @SerializedName("4")
    OnСonfirmation("4")
}