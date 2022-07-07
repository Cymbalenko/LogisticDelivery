package com.example.logisticdelivery.model.api

import com.example.logisticdelivery.model.api.responses.*
import com.example.logisticdelivery.ui.properties.AppProperties
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*

public interface ApiService {

    @GET("search")
    fun getRecepts(
        @Query("q") q:String
    ): Single<List<DeliveryOrders>>

    @GET("search")
    fun getListRecepts(
        @Query("q") q:String
    ): Single<OrderList>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("token")
    fun autorization(
            @Query("username") username:String,
            @Query("password") password:String
    ): Single<String>

    @GET("api/ApiAccount/testConnected")
    fun isEnabledApi(
    ): Single<Boolean>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("is_register")
    fun isRegister(
        @Query("staffId") staffId:String,
        @Query("password") password:String,
        @Header("Authorization") token:String
    ): Single<Boolean>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("setCrashReport")
    fun setCrashReport(
        @Query("value") value:String,
        @Header("Authorization") token:String
    ): Single<Boolean>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("driver_status")
    fun setDriverStatus(
            @Query("id") id:String,
            @Query("statusId") statusId:String,
            @Header("Authorization") token:String
    ): Observable<Response<Void>>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("getStatusDriver")
    fun getStatusDriver(
            @Query("id") id:String,
            @Header("Authorization") token:String
    ): Single<Int>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("api/DeliveryOrder/get_Qr_Link_Pay")
    fun getQrLinkPay(
            @Query("deliveryOrderId") id:String,
            @Header("Authorization") token:String
    ): Single<String>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("api/DeliveryOrder/sendDeliveryOrderStatus")
    fun sendDeliveryOrderStatus(
            @Query("id") id:String,
            @Query("statusId") statusId:String,
            @Header("Authorization") token:String
    ): Observable<Response<Void>>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("api/DeliveryOrder/get_Delivery_Order_Driver_last")
    fun getDeliveryOrderDriver(
            @Query("driverId") driverId:String,
            @Header("Authorization") token:String
    ): Single<DeliveryOrders>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("get_Drivers_Read_StaffId")
    fun getDriver(
            @Query("id") driverId:String,
            @Header("Authorization") token:String
    ): Single<DriverApi>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("api/DriverGeolocation")
    fun sendDriverGeolocation(
       @Body  driverGeolocation: DriverGeolocation,
       @Header("Authorization") token:String
    ): Single<Void>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("api/DeliveryOrder/{id}")
    fun getDeliveryOrder(
            @Path("id") id: String,
            @Header("Authorization") token:String
    ): Single<DeliveryOrders>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("api/RestaurantGeolocation/{id}")
    fun getRestaurantGeolocation(
            @Path("id") id: Int,
            @Header("Authorization") token:String
    ): Single<RestaurantGeolocation>

    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @GET("getAllReadDriverAsync")
    fun getRestauranList(
            @Header("Authorization") token:String
    ): Single<List<RestaurantApi>>
    @Headers(  "Content-Type: application/json;charset=UTF-8" )
    @POST("set_Drivers_Read_Phone")
    fun setDriverPhone(
            @Query("id") id: String,
            @Query("phone") phone: String,
            @Header("Authorization") token:String
    ): Observable<Response<Void>>
}