package com.example.roombill

import android.app.Application
import com.example.roombill.data.local.AppDatabase
import com.example.roombill.data.repository.RoomBillRepository
import com.example.roombill.data.repository.RoomBillRepositoryImpl

class RoomBillApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: RoomBillRepository by lazy {
        RoomBillRepositoryImpl(database.configDao(), database.billDao())
    }
}
