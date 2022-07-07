package com.example.logisticdelivery.model.api.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class DriverGeolocation(
        @SerializedName("driverGeolocationId")
        val driverGeolocationId:String,
        @SerializedName("latitude")
        val latitude:Double= 0.0,
        @SerializedName("longitude")
        val longitude:Double= 0.0,
        @SerializedName("createdDateTime")
        val createdDateTime:String,
        @SerializedName("lastModifyDateTime")
        val lastModifyDateTime:String,
        @SerializedName("driverId")
        val driverId:Int,
        @SerializedName("driverName")
        val driverName:String,
        @SerializedName("driverStaffId")
        val driverStaffId:String
){
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
