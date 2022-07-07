package com.example.logisticdelivery.model.api.responses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

data class DeliveryOrderRoute(
    @SerializedName("deliveryOrderRouteId")
    val deliveryOrderRouteIdS:String,
    @SerializedName("estimatedStartTimeRoute")
    val estimatedStartTimeRoute: String,
    @SerializedName("estimatedFinishTimeRoute")
    val estimatedFinishTimeRoute:String,
    @SerializedName("actualStartTimeRoute")
    val actualStartTimeRoute: String,
    @SerializedName("actualFinishTimeRoute")
    var actualFinishTimeRoute:String,
    @SerializedName("distanceToClient")
    val distanceToClient:Int,
    @SerializedName("deliveryOrderId")
    val deliveryOrderIdRoute:String,
    @SerializedName("deliveryOrderReceptNo")
    val deliveryOrderReceptNo:String,
    @SerializedName("timeDistanceToClient")
    val timeDistanceToClient:String,
    @Embedded
    @SerializedName("deliveryOrderGeolocation")
    val deliveryOrderGeolocation:DeliveryOrderGeolocation?=null
){
    @PrimaryKey(autoGenerate = true)
    var idRoute: Int = 0
}
