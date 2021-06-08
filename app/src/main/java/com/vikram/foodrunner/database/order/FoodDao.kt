package com.vikram.foodrunner.database.order

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {
    @Insert
    fun insert(foodEntity: FoodEntity)

    @Delete
    fun delete(foodEntity: FoodEntity)

    @Query("SELECT * FROM food")
    fun getAllFood(): List<FoodEntity>

    @Query("SELECT * FROM food WHERE id = :id")
    fun getFoodById(id:String): FoodEntity

    @Query("DELETE FROM food")
    fun deleteFood()
}