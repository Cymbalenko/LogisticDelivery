package com.example.logisticdelivery.model.api.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.logisticdelivery.model.api.responses.responsesEnum.DriverStatus
import com.example.logisticdelivery.model.api.responses.responsesEnum.DriverType
import com.google.gson.annotations.SerializedName

@Entity
data class DriverApi (
        @SerializedName("driverId")
        val driverId:String,
        @SerializedName("fullName")
        val fullName:String? = null,
        @SerializedName("phone")
        val phone:String? = null,
        @SerializedName("password")
        val password:String? = null,
        @SerializedName("staffId")
        val staffId:String? = null,
        @SerializedName("driverType")
        val driverType:DriverType,
        @SerializedName("driverStatus")
        val driverStatus:DriverStatus,
        @SerializedName("defaultRestaurantNo")
        val defaultRestaurantNo:String? = null,
        /*@SerializedName("restaurants")
        val restaurants:List<String>? = null*/
) {
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}