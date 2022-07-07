package com.example.logisticdelivery.model.repository

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Room
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import com.example.logisticdelivery.model.api.responses.DriverApi
import com.example.logisticdelivery.model.api.responses.RestaurantApi
import com.example.logisticdelivery.model.api.responses.RestaurantGeolocation
import com.example.logisticdelivery.model.api.responses.responsesEnum.DeliveryOrderStatus
import com.example.logisticdelivery.model.db.OrdersDatabase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

object RoomRepository {
    private val db: OrdersDatabase = createDatabase()
    //DeliveryOrder
    fun insertDeliveryOrders(orders: DeliveryOrders): Single<Long> {
        return db.deliveryOrdersDao.insert(orders)
    }

    fun insertIsNotDeliveryOrders(orders: DeliveryOrders): Single<Long> {

        return db.deliveryOrdersDao.insert(orders)
    }
    fun updateDeliveryOrders(orders: DeliveryOrders): Completable {
        return db.deliveryOrdersDao.update(orders)
    }

    fun getDeliveryOrderByOrderId(no: String): Single<DeliveryOrders> {
        return db.deliveryOrdersDao.getDeliveryOrderByOrderId(no)
    }

    fun deleteByIdDeliveryOrders(order: DeliveryOrders): Completable {
        return db.deliveryOrdersDao.delete(order)
    }

    fun deleteByOrderIdDeliveryOrders(no: String): Completable{
        return db.deliveryOrdersDao.deleteByOrderId(no)
    }
    fun getNewDeliveryOrders(): LiveData<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getNewDeliveryOrders()
    }
    fun getNewDeliveryOrders(driverId:String): LiveData<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getNewDeliveryOrders(driverId)
    }
    fun deleteByIdQueryDeliveryOrders(id: Int): Completable{
        return db.deliveryOrdersDao.deleteByIdQuery(id)
    }

    fun getAllDeliveryOrders(): LiveData<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getAllDeliveryOrders()
    }
    fun getAllDeliveryOrdersSingle(): Single<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getAllDeliveryOrdersSingle()
    }

    fun getDeliveryOrdersHistory(date:String,driverId:String):  List<DeliveryOrders>  {
        return db.deliveryOrdersDao.getDeliveryOrdersHistory(date, driverId)
    }

    fun getDeliveryOrdersHistory():  LiveData<List<DeliveryOrders>>  {
        return db.deliveryOrdersDao.getDeliveryOrdersHistory( )
    }

    fun getDeliveryOrdersHistoryDay(day:String):  Single<List<DeliveryOrders>>  {
        return db.deliveryOrdersDao.getDeliveryOrdersHistoryDay(day)
    }
    fun getDeliveryOrdersHistoryDays():   Single<List<DeliveryOrders>>   {
        return db.deliveryOrdersDao.getDeliveryOrdersHistoryDays()
    }
    fun getHistoryDeliveryOrdersCount(staffId:String): LiveData<Int> {
        return db.deliveryOrdersDao.getHistoryDeliveryOrdersCount(staffId)
    }

    fun getAllDeliveryOrdersAsLiveData(): LiveData<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getAllDeliveryOrders()
    }

    fun getContactById(id: Int): Single<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getDeliveryOrderById(id)
    }

    fun updateByDriverIdPhone(phone: String,driverId: String): Completable {
        return db.driversDao.updateByDriverIdPhone(phone,driverId)
    }
    fun updateByDriverIdStatus(status:String,driverId: String): Completable {
        return db.driversDao.updateByDriverIdStatus(status,driverId)
    }

    fun getContactByOrderId(id: String): Single<DeliveryOrders> {
        return db.deliveryOrdersDao.getDeliveryOrderByOrderId(id)
    }

    fun getDeliveryOrdersToday(): LiveData<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getDeliveryOrdersToday()
    }



    fun updateDeliveredOrder(order: DeliveryOrders):Completable{

        return db.deliveryOrdersDao.update(order)
    }
    fun updateCanDeliveredOrder(order: DeliveryOrders):Completable{
        order.let {
            it.deliveryOrderStatus = DeliveryOrderStatus.Ready
        }
        return db.deliveryOrdersDao.update(order)
    }
    //Driver
    fun insertDriver(driver: DriverApi): Single<Long> {
        return db.driversDao.insert(driver)
    }
    fun getAllDrivers(): LiveData<List<DriverApi>> {
        return db.driversDao.getAllDriverApi()
    }
    fun isDriver(driverId:String): Single<DriverApi> {
        return db.driversDao.getDriverByDriverId(driverId)
    }
    fun updateDriver(driver: DriverApi): Completable {
        return db.driversDao.update(driver)
    }

    //Restaurant
    fun initRestaurant(restarant:RestaurantApi): Single<Long> {
        return db.restaurantApiDao.insert(restarant)
    }
    fun updateRestaurant(restarant:RestaurantApi): Completable {
        return db.restaurantApiDao.update(restarant)
    }
    fun deleteRestaurantAll(id:Int): Completable {
        return db.restaurantApiDao.deleteByIdQueryAll(id)
    }
    fun isRestaurant(externalNo:String): Single<RestaurantApi> {
         return db.restaurantApiDao.getRestaurantApiByRestaurantId(externalNo)
    }
    fun getRestaurantsApi(): Single<List<RestaurantApi>> {
         return db.restaurantApiDao.getRestaurantsApi()
    }

    //RestaurantGps
    fun initRestaurantGps(restarantGeo:RestaurantGeolocation): Single<Long> {
        return db.restaurantGeolocationDao.insert(restarantGeo)
    }
    fun updateRestaurantGps(restarantGeo:RestaurantGeolocation): Completable {
        return db.restaurantGeolocationDao.update(restarantGeo)
    }
    fun isRestaurantGps(externalNo:String): Single<RestaurantGeolocation> {
        return db.restaurantGeolocationDao.getRestaurantGeoApi(externalNo)
    }
    fun getRestaurantsApiGps(): LiveData<List<RestaurantGeolocation>> {
        return db.restaurantGeolocationDao.getRestaurantsGeoApi()
    }

    private fun createDatabase(): OrdersDatabase {
        return Room.databaseBuilder(
            LogisticDeliveryApplication.instance!!,
            OrdersDatabase::class.java,
            OrdersDatabase.NAME
        )
            .fallbackToDestructiveMigration().build()
    }
}