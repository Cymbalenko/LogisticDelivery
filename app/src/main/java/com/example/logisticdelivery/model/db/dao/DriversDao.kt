package com.example.logisticdelivery.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.logisticdelivery.model.api.responses.DriverApi
import com.example.logisticdelivery.model.api.responses.RestaurantApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface DriversDao  {

    @Insert
    fun insert(driver: DriverApi): Single<Long>

    @Update
    fun update(driver: DriverApi): Completable

    @Query("DELETE FROM driverApi WHERE id = :id")
    fun deleteByIdQuery(id: Int): Completable

    @Delete
    fun delete(driver: DriverApi): Completable

    @Query("DELETE FROM driverApi WHERE staffId = :no")
    fun deleteByDriverId(no: String): Completable

    @Query("SELECT * FROM driverApi")
    fun getAllDriverApi(): LiveData<List<DriverApi>>


    @Query("SELECT *  FROM driverapi WHERE staffId = :staffId")
    fun getDriverByDriverId(staffId: String): Single<DriverApi>

    @Query("UPDATE DriverApi SET phone=:no WHERE driverId=:driverId")
    fun updateByDriverIdPhone(no: String,driverId:String): Completable

    @Query("UPDATE DriverApi SET driverStatus=:status WHERE driverId=:driverId")
    fun updateByDriverIdStatus(status: String,driverId:String): Completable
}