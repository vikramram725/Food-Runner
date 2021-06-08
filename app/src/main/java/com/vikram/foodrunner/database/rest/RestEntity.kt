package com.vikram.foodrunner.database.rest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class RestEntity (
    @PrimaryKey val restId: String,
    @ColumnInfo(name = "name") val restName:String,
    @ColumnInfo(name = "rating") val rating: String,
    @ColumnInfo(name = "cost_for_one") val costPer:String,
    @ColumnInfo(name = "image_url") val image: String
)