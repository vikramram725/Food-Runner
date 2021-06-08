package com.vikram.foodrunner.database.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class FoodEntity (
    @PrimaryKey val id: String,
    @ColumnInfo val serialId: String,
    @ColumnInfo val name: String,
    @ColumnInfo val cost: String,
    @ColumnInfo val restId: String
)