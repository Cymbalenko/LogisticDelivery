package com.example.logisticdelivery.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface DeliveryOrdersDao {

    @Insert
    fun insert(deliveryOrders: DeliveryOrders): Single<Long>

    @Update
    fun update(deliveryOrders: DeliveryOrders): Completable

    @Query("DELETE FROM deliveryorders WHERE id = :id")
    fun deleteByIdQuery(id: Int): Completable
    @Delete
    fun delete(orders: DeliveryOrders): Completable
    @Query("DELETE FROM deliveryorders WHERE receptNo = :no")
    fun deleteByOrderId(no: String): Completable

    @Query("SELECT * FROM deliveryorders")
    fun getAllDeliveryOrders(): LiveData<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders")
    fun getAllDeliveryOrdersSingle(): Single<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders where orderDateTime >= CURRENT_DATE or deliveryOrderStatus=7")
    fun getDeliveryOrdersToday(): LiveData<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders where (orderDateTime = date('now','-'+:date+' day') and driverId=:driverId  and (deliveryOrderStatus=5 or deliveryOrderStatus=6))")
    fun getDeliveryOrdersHistory(date:String,driverId:String): List<DeliveryOrders>

    @Query("SELECT * FROM deliveryorders where  deliveryOrderStatus='Ready' OR deliveryOrderStatus='Delivery' ")
    fun getNewDeliveryOrders( ): LiveData<List<DeliveryOrders>>
    @Query("SELECT * FROM deliveryorders where  (deliveryOrderStatus='Ready' OR deliveryOrderStatus='Delivery') AND driverId=:driverId ")
    fun getNewDeliveryOrders(driverId:String): LiveData<List<DeliveryOrders>>

    @Query("SELECT Count(*) FROM deliveryorders where  orderDateTime = CURRENT_DATE  AND (deliveryOrderStatus !='Ready' AND deliveryOrderStatus != 'Delivery') AND driverId=:staffId")
    fun getHistoryDeliveryOrdersCount(staffId:String): LiveData<Int>

    @Query("SELECT * FROM deliveryorders where id>0")
    //@Query("SELECT * FROM deliveryorders where (date(orderDateTime) = date('now','-'+:date+' day') and  (deliveryOrderStatus=5 or deliveryOrderStatus=6))")
    fun getDeliveryOrdersHistory(): LiveData<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders where orderDateTime LIKE (:day) || '%'")
    //@Query("SELECT * FROM deliveryorders where (date(orderDateTime) = date('now','-'+:date+' day') and  (deliveryOrderStatus=5 or deliveryOrderStatus=6))")
    fun getDeliveryOrdersHistoryDay(day:String): Single<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders where orderDateTime LIKE ('2021-12-07') || '%'")
    //@Query("SELECT * FROM deliveryorders where (date(orderDateTime) = date('now','-'+:date+' day') and  (deliveryOrderStatus=5 or deliveryOrderStatus=6))")
    fun getDeliveryOrdersHistoryDays():  Single<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders WHERE id = :id")
    fun getDeliveryOrderById(id: Int): Single<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders WHERE receptNo = :no")
    fun getDeliveryOrderByOrderId(no: String): Single<DeliveryOrders>

}