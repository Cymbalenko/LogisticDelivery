package com.example.logisticdelivery.model.api.responses.responsesEnum

import com.google.gson.annotations.SerializedName

enum class IncidentType (val value: String) {
    @SerializedName("0")
    NoIncidend("0"),
    @SerializedName("1")
    TrafficAccident("1"),
    @SerializedName("2")
    CourierCancelled("2"),
    @SerializedName("3")
    RestaurantLateness("3"),
    @SerializedName("4")
    CourierLateness("4")
}