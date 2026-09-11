package com.example.roombill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configs")
data class ConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val roomRent: Double = 0.0,
    val electricityUnitPrice: Double = 0.0,
    val waterUnitPrice: Double = 0.0,
    val internetFee: Double = 0.0,
    val garbageFee: Double = 0.0,
    val extraFee: Double = 0.0
)
