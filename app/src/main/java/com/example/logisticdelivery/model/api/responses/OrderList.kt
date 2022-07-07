package com.example.logisticdelivery.model.api.responses

import com.google.gson.annotations.SerializedName

data class OrderList(
    @SerializedName("recipes")
    val listRecept :List<DeliveryOrders>
)