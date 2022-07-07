package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class DeliveryOrderStatus (var value: String) {
    @SerializedName("0")
    New("0"),
    @SerializedName("1")
    Pause("1"),
    @SerializedName("2")
    AwaitingCourierAppointment("2"),
    @SerializedName("3")
    WaitingStartCooking("3"),
    @SerializedName("4")
    Preparing("4"),
    @SerializedName("5")
    Ready("5"),
    @SerializedName("6")
    Delivery("6"),
    @SerializedName("7")
    Delivered("7"),
    @SerializedName("8")
    Completed("8"),
    @SerializedName("9")
    Canceled("9"),
    @SerializedName("10")
    Error("10")
}