package com.vikram.foodrunner.database.rest

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestDao {
    @Insert
    fun insert(restEntity: RestEntity)

    @Delete
    fun delete(restEntity: RestEntity)

    @Query("SELECT * FROM restaurant")
    fun getAllRest():List<RestEntity>

    @Query("SELECT * FROM restaurant WHERE restId = :restId")
    fun getRestById(restId:String): RestEntity
}