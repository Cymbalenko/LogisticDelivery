package com.example.logisticdelivery.model.api.responses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.logisticdelivery.model.api.responses.responsesEnum.DeliveryOrderStatus
import com.example.logisticdelivery.model.api.responses.responsesEnum.IncidentType
import com.example.logisticdelivery.model.api.responses.responsesEnum.TenderStatus
import com.example.logisticdelivery.model.api.responses.responsesEnum.TenderType
import com.google.gson.annotations.SerializedName

@Entity
data class DeliveryOrders(
    @SerializedName("deliveryOrderId")
    val deliveryOrderId:String,
    @SerializedName("receptNo")
    val receptNo:String,
    @SerializedName("phone")
    val phone:String,
    @SerializedName("name")
    val name:String,
    @SerializedName("preOrder")
    val preOrder:Boolean,
    @SerializedName("orderDateTime")
    val orderDateTime:String?=null,
    @SerializedName("appointmentDateTime")
    val appointmentDateTime:String?=null,
    @SerializedName("appointment")
    val appointment:Boolean,
    @SerializedName("amount")
    val amount:Double,
    @SerializedName("estimatedOrderReadinessTime")
    val estimatedOrderReadinessTime:String?=null,
    @SerializedName("createdDateTime")
    val createdDateTime:String?=null,
    @SerializedName("streetName")
    val streetName:String?=null,
    @SerializedName("city")
    val city:String?=null,
    @SerializedName("direction")
    val direction:String?=null,
    @SerializedName("externalNo")
    val externalNo:String?=null,
    @SerializedName("driverId")
    var driverId:Int?=null,
    @SerializedName("streetNo")
    val streetNo:String?=null,
    @SerializedName("approximateTimeOrderReadyRestaurant")
    val approximateTimeOrderReadyRestaurant:String?=null,
    @SerializedName("waitingTimeOrderByClient")
    val waitingTimeOrderByClient:String?=null,
    @SerializedName("actualTimeOrderDeliveryByDriver")
    val actualTimeOrderDeliveryByDriver:String?=null,
    @SerializedName("actualTimeStartDelivery")
    val actualTimeStartDelivery:String?=null,
    @SerializedName("actualTimeOrderReadiness")
    val actualTimeOrderReadiness:String?=null,
    @SerializedName("sendCookingTime")
    val sendCookingTime:String?=null,
    @SerializedName("scheduledDeliveryTime")
    val scheduledDeliveryTime:String?=null,
    @SerializedName("additionalData")
    val additionalData:String?=null,
    @SerializedName("incidentType")
    val incidentType:IncidentType,
    @SerializedName("tenderType")
    val tenderType:TenderType,
    @SerializedName("tenderStatus")
    val tenderStatus:TenderStatus,
    @SerializedName("deliveryOrderStatus")
    var deliveryOrderStatus:DeliveryOrderStatus,
    @SerializedName("incidentDescription")
    val incidentDescription:String?=null,
    @SerializedName("restaurantId")
    val restaurantId:Int?=0,
    @SerializedName("restaurantName")
    val restaurantName:String?=null,
    @Embedded
    @SerializedName("deliveryOrderRoute")
    val deliveryOrderRoute:DeliveryOrderRoute?=null

){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}