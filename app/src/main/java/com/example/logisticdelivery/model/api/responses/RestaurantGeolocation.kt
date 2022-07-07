package com.example.logisticdelivery.model.api.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class RestaurantGeolocation(
        @SerializedName("restaurantGeolocationId")
        val restaurantGeolocationId:String,
        @SerializedName("latitude")
        val latitude:Double=0.0,
        @SerializedName("longitude")
        val longitude:Double=0.0,
        @SerializedName("createdDateTime")
        val createdDateTime:String,
        @SerializedName("lastModifyDateTime")
        val lastModifyDateTime:String,
        @SerializedName("restaurantId")
        val restaurantId:Int,
        @SerializedName("restaurantName")
        val restaurantName:String,
        @SerializedName("externalNo")
        val externalNo:String
){
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
