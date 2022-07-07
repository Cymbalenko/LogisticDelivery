package com.example.logisticdelivery.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import com.example.logisticdelivery.model.api.responses.DriverApi
import com.example.logisticdelivery.model.api.responses.RestaurantApi
import com.example.logisticdelivery.model.api.responses.RestaurantGeolocation
import com.example.logisticdelivery.model.db.dao.DeliveryOrdersDao
import com.example.logisticdelivery.model.db.dao.DriversDao
import com.example.logisticdelivery.model.db.dao.RestaurantApiDao
import com.example.logisticdelivery.model.db.dao.RestaurantGeolocationDao

@Database(entities =
[
    DeliveryOrders::class,
    DriverApi::class,
    RestaurantGeolocation::class,
    RestaurantApi::class
], version = 13)
abstract class OrdersDatabase : RoomDatabase() {

    companion object {
        const val NAME = "delivery_orders_db"
    }

    abstract val deliveryOrdersDao: DeliveryOrdersDao
    abstract val driversDao: DriversDao
    abstract val restaurantApiDao: RestaurantApiDao
    abstract val restaurantGeolocationDao: RestaurantGeolocationDao
}