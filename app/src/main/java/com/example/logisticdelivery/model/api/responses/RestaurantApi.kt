package com.example.logisticdelivery.model.api.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.logisticdelivery.model.api.responses.responsesEnum.RestaurantStatus
import com.google.gson.annotations.SerializedName

@Entity
data class RestaurantApi(
        @SerializedName("restaurantId")
        val restaurantId:String,
        @SerializedName("description")
        val description:String,
        @SerializedName("phone")
        val phone:String,
        @SerializedName("externalNo")
        val externalNo:String,
        @SerializedName("restaurantStatus")
        val restaurantStatus:RestaurantStatus,
        @SerializedName("restaurantGeolocationId")
        val restaurantGeolocationId:Int=0,
        @SerializedName("organizationId")
        val organizationId:Int=0,
        @SerializedName("organizationName")
        val organizationName:String?=null
){
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
