package com.example.logisticdelivery.model.repository

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.api.ApiService
import com.example.logisticdelivery.model.api.responses.*
import com.example.logisticdelivery.ui.properties.AppProperties
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository:ApiService {

    private val apiService = createApiService()


    fun getRecept(q:String): Single<List<DeliveryOrders>> {
        return apiService.getRecepts(q)
    }

    override fun getRecepts(q: String): Single<List<DeliveryOrders>> {
        TODO("Not yet implemented")
    }

    override fun getListRecepts(q:String): Single<OrderList> {
        return apiService.getListRecepts(q)
    }

    override fun autorization(username: String, password: String): Single<String> {
        return apiService.autorization(username,password)
    }

    override fun isEnabledApi(): Single<Boolean> {
        return apiService.isEnabledApi()
    }

    override fun isRegister(staffId: String, password: String,token: String): Single<Boolean> {
        return apiService.isRegister(staffId, password, token)
    }

    override fun setCrashReport(value: String, token: String): Single<Boolean> {
        return apiService.setCrashReport(value, token);
    }

    override fun setDriverStatus(id: String, statusId: String,token: String): Observable<Response<Void>> {
        return apiService.setDriverStatus(id, statusId, token)
    }

    override fun getDriver(driverId: String, token: String): Single<DriverApi> {
        return apiService.getDriver(driverId,token)
    }

    override fun getQrLinkPay(id: String,token: String): Single<String> {
        return apiService.getQrLinkPay(id, token)
    }

    override fun sendDeliveryOrderStatus(id: String, statusId: String,token: String): Observable<Response<Void>> {
        return apiService.sendDeliveryOrderStatus(id, statusId, token)
    }

    override fun getStatusDriver(id: String, token: String): Single<Int> {
        return apiService.getStatusDriver( id,token)
    }

    override fun getDeliveryOrderDriver(driverId: String,token: String): Single<DeliveryOrders> {
        return apiService.getDeliveryOrderDriver(driverId, token)
    }

    override fun sendDriverGeolocation(driverGeolocation: DriverGeolocation,token: String): Single<Void> {
        return apiService.sendDriverGeolocation(driverGeolocation, token)
    }

    override fun getDeliveryOrder(id: String, token: String): Single<DeliveryOrders> {
         return apiService.getDeliveryOrder(id,token)
    }

    override fun getRestaurantGeolocation(id: Int, token: String): Single<RestaurantGeolocation> {
        return apiService.getRestaurantGeolocation(id,token)
    }

    override fun getRestauranList(token: String): Single<List<RestaurantApi>> {
        return apiService.getRestauranList(token)
    }

    override fun setDriverPhone(id: String, phone: String, token: String): Observable<Response<Void>> {
        return apiService.setDriverPhone(id, phone, token)
    }
    private fun createRetrofit(): Retrofit {
        LogisticDeliveryApplication.instance?.let {
            val preferences = it.getSharedPreferences(AppProperties.APP_PREFERENCES, Context.MODE_PRIVATE)
            val adress = preferences.getString("address","http://88.198.110.32")
            val port =  preferences.getString("port", "8008")
            val baseUrl = adress+":"+port+"/"
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
        }
        return Retrofit.Builder()
                .baseUrl("http://88.198.110.32:8008/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
    }

    private fun createApiService(): ApiService {
        val retrofit= createRetrofit()
        return retrofit.create(ApiService::class.java)
    }
}