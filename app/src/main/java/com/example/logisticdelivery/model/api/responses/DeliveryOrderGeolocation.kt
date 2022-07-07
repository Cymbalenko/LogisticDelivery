package com.example.logisticdelivery.model.api.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class DeliveryOrderGeolocation(
    @SerializedName("deliveryOrderGeolocationId")
    val deliveryOrderGeolocationIdS:String,
    @SerializedName("latitude")
    val latitude:Double= 0.0,
    @SerializedName("longitude")
    val longitude:Double= 0.0,
    @SerializedName("deliveryOrderRouteId")
    val deliveryOrderRouteId:Int
){
    @PrimaryKey(autoGenerate = true)
    var idGeolocation: Int = 0
}