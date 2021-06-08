package com.vikram.foodrunner.database.rest

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestEntity::class],version = 1)
abstract class RestDatabase: RoomDatabase(){
    abstract fun restDao(): RestDao
}