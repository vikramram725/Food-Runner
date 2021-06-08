package com.vikram.foodrunner.database.order

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntity::class],version = 1)
abstract class FoodDatabase: RoomDatabase() {
    abstract fun foodDao(): FoodDao
}