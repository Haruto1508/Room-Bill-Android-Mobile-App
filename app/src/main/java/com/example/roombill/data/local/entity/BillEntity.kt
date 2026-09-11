package com.example.roombill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val month: Int,
    val year: Int,
    val prevElectricityReading: Double,
    val currElectricityReading: Double,
    val electricityCost: Double,
    val prevWaterReading: Double,
    val currWaterReading: Double,
    val waterCost: Double,
    val roomRentCost: Double,
    val internetCost: Double,
    val garbageCost: Double,
    val extraCost: Double,
    val extraCostDescription: String = "",
    val totalAmount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
