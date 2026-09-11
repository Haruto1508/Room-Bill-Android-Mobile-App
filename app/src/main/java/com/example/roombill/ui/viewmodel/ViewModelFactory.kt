package com.example.roombill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roombill.data.repository.RoomBillRepository

class ViewModelFactory(
    private val repository: RoomBillRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ConfigViewModel::class.java) -> {
                ConfigViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BillCalculatorViewModel::class.java) -> {
                BillCalculatorViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BillHistoryViewModel::class.java) -> {
                BillHistoryViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
