package com.example.roombill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.repository.RoomBillRepository
import com.example.roombill.utils.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class BillCalculatorUiState(
    val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val prevElectricityReading: String = "0",
    val currElectricityReading: String = "0",
    val electricityUnitPrice: Double = 0.0,
    val prevWaterReading: String = "0",
    val currWaterReading: String = "0",
    val waterUnitPrice: Double = 0.0,
    val roomRentCost: String = "0",
    val internetCost: String = "0",
    val garbageCost: String = "0",
    val extraCost: String = "0",
    val extraCostDescription: String = "",
    val isAutoPopulated: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val userMessage: String? = null
) {
    val prevElectricity: Double
        get() = FormatUtils.parseFormattedDouble(prevElectricityReading)

    val currElectricity: Double
        get() = FormatUtils.parseFormattedDouble(currElectricityReading)

    val electricityUsage: Double
        get() = maxOf(0.0, currElectricity - prevElectricity)

    val electricityCost: Double
        get() = electricityUsage * electricityUnitPrice

    val prevWater: Double
        get() = FormatUtils.parseFormattedDouble(prevWaterReading)

    val currWater: Double
        get() = FormatUtils.parseFormattedDouble(currWaterReading)

    val waterUsage: Double
        get() = maxOf(0.0, currWater - prevWater)

    val waterCost: Double
        get() = waterUsage * waterUnitPrice

    val roomRent: Double
        get() = FormatUtils.parseFormattedDouble(roomRentCost)

    val internet: Double
        get() = FormatUtils.parseFormattedDouble(internetCost)

    val garbage: Double
        get() = FormatUtils.parseFormattedDouble(garbageCost)

    val extra: Double
        get() = FormatUtils.parseFormattedDouble(extraCost)

    val totalAmount: Double
        get() = roomRent + electricityCost + waterCost + internet + garbage + extra
}

class BillCalculatorViewModel(
    private val repository: RoomBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillCalculatorUiState())
    val uiState: StateFlow<BillCalculatorUiState> = _uiState.asStateFlow()

    private var initialPopulateDone = false

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getConfig(),
                repository.getLatestBillFlow()
            ) { config, latestBill ->
                Pair(config, latestBill)
            }.collect { (config, latestBill) ->
                _uiState.update { state ->
                    var newState = state

                    if (config != null) {
                        newState = newState.copy(
                            electricityUnitPrice = config.electricityUnitPrice,
                            waterUnitPrice = config.waterUnitPrice
                        )

                        if (!initialPopulateDone) {
                            newState = newState.copy(
                                roomRentCost = FormatUtils.formatNumber(config.roomRent),
                                internetCost = FormatUtils.formatNumber(config.internetFee),
                                garbageCost = FormatUtils.formatNumber(config.garbageFee),
                                extraCost = FormatUtils.formatNumber(config.extraFee)
                            )
                        }
                    }

                    if (latestBill != null && !initialPopulateDone) {
                        val nextMonth = if (latestBill.month == 12) 1 else latestBill.month + 1
                        val nextYear = if (latestBill.month == 12) latestBill.year + 1 else latestBill.year

                        newState = newState.copy(
                            prevElectricityReading = FormatUtils.formatNumber(latestBill.currElectricityReading),
                            prevWaterReading = FormatUtils.formatNumber(latestBill.currWaterReading),
                            currElectricityReading = FormatUtils.formatNumber(latestBill.currElectricityReading),
                            currWaterReading = FormatUtils.formatNumber(latestBill.currWaterReading),
                            month = nextMonth,
                            year = nextYear,
                            isAutoPopulated = true
                        )
                    }

                    if (config != null || latestBill != null) {
                        initialPopulateDone = true
                    }

                    newState
                }
            }
        }
    }

    fun updateMonth(month: Int) {
        _uiState.update { it.copy(month = month, isSaved = false) }
    }

    fun updateYear(year: Int) {
        _uiState.update { it.copy(year = year, isSaved = false) }
    }

    fun updatePrevElectricityReading(value: String) {
        _uiState.update { it.copy(prevElectricityReading = value, isSaved = false) }
    }

    fun updateCurrElectricityReading(value: String) {
        _uiState.update { it.copy(currElectricityReading = value, isSaved = false) }
    }

    fun updatePrevWaterReading(value: String) {
        _uiState.update { it.copy(prevWaterReading = value, isSaved = false) }
    }

    fun updateCurrWaterReading(value: String) {
        _uiState.update { it.copy(currWaterReading = value, isSaved = false) }
    }

    fun updateRoomRentCost(value: String) {
        _uiState.update { it.copy(roomRentCost = value, isSaved = false) }
    }

    fun updateInternetCost(value: String) {
        _uiState.update { it.copy(internetCost = value, isSaved = false) }
    }

    fun updateGarbageCost(value: String) {
        _uiState.update { it.copy(garbageCost = value, isSaved = false) }
    }

    fun updateExtraCost(value: String) {
        _uiState.update { it.copy(extraCost = value, isSaved = false) }
    }

    fun updateExtraCostDescription(desc: String) {
        _uiState.update { it.copy(extraCostDescription = desc, isSaved = false) }
    }

    fun saveBill() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isSaving = true) }

            val bill = BillEntity(
                month = state.month,
                year = state.year,
                prevElectricityReading = state.prevElectricity,
                currElectricityReading = state.currElectricity,
                electricityCost = state.electricityCost,
                prevWaterReading = state.prevWater,
                currWaterReading = state.currWater,
                waterCost = state.waterCost,
                roomRentCost = state.roomRent,
                internetCost = state.internet,
                garbageCost = state.garbage,
                extraCost = state.extra,
                extraCostDescription = state.extraCostDescription,
                totalAmount = state.totalAmount,
                createdAt = System.currentTimeMillis()
            )

            repository.insertBill(bill)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    isSaved = true,
                    userMessage = "Lưu hóa đơn tháng ${state.month}/${state.year} thành công!"
                )
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
