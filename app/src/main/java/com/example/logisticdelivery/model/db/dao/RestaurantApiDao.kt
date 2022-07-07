package com.example.logisticdelivery.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.logisticdelivery.model.api.responses.RestaurantApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface RestaurantApiDao   {

    @Insert
    fun insert(restaurant: RestaurantApi): Single<Long>

    @Update
    fun update(restaurant: RestaurantApi): Completable

    @Query("DELETE FROM restaurantApi WHERE id = :id")
    fun deleteByIdQuery(id: Int): Completable

    @Query("DELETE FROM restaurantApi WHERE id > :id")
    fun deleteByIdQueryAll(id: Int): Completable

    @Delete
    fun delete(restaurant: RestaurantApi): Completable

    @Query("DELETE FROM restaurantApi WHERE externalNo = :no")
    fun deleteByRestaurantId(no: String): Completable

    @Query("SELECT * FROM restaurantApi")
    fun getRestaurantsApi(): Single<List<RestaurantApi>>

    @Query("SELECT *  FROM restaurantApi WHERE externalNo = :extension")
    fun getRestaurantApiByRestaurantId(extension: String): Single<RestaurantApi>

}