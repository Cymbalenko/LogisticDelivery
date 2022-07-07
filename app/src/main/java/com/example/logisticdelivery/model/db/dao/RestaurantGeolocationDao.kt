package com.example.logisticdelivery.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.logisticdelivery.model.api.responses.RestaurantGeolocation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface RestaurantGeolocationDao {

    @Insert
    fun insert(restaurantGeo: RestaurantGeolocation): Single<Long>

    @Update
    fun update(restaurantGeo: RestaurantGeolocation): Completable

    @Query("DELETE FROM restaurantGeolocation WHERE id = :id")
    fun deleteByIdQuery(id: Int): Completable

    @Delete
    fun delete(restaurantGeo: RestaurantGeolocation): Completable

    @Query("DELETE FROM restaurantGeolocation WHERE externalNo = :no")
    fun deleteByRestaurantGeoId(no: String): Completable

    @Query("SELECT * FROM restaurantGeolocation")
    fun getRestaurantsGeoApi(): LiveData<List<RestaurantGeolocation>>

    @Query("SELECT * FROM restaurantGeolocation WHERE externalNo = :no")
    fun getRestaurantGeoApi(no: String): Single<RestaurantGeolocation>

}