package com.example.roombill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roombill.data.local.entity.ConfigEntity
import com.example.roombill.data.repository.RoomBillRepository
import com.example.roombill.utils.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfigUiState(
    val roomRent: String = "",
    val electricityUnitPrice: String = "",
    val waterUnitPrice: String = "",
    val internetFee: String = "",
    val garbageFee: String = "",
    val extraFee: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val userMessage: String? = null
)

class ConfigViewModel(
    private val repository: RoomBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState: StateFlow<ConfigUiState> = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    fun loadConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getConfig().collect { config ->
                if (config != null) {
                    _uiState.update { state ->
                        state.copy(
                            roomRent = FormatUtils.formatNumber(config.roomRent),
                            electricityUnitPrice = FormatUtils.formatNumber(config.electricityUnitPrice),
                            waterUnitPrice = FormatUtils.formatNumber(config.waterUnitPrice),
                            internetFee = FormatUtils.formatNumber(config.internetFee),
                            garbageFee = FormatUtils.formatNumber(config.garbageFee),
                            extraFee = FormatUtils.formatNumber(config.extraFee),
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun updateRoomRent(value: String) {
        _uiState.update { it.copy(roomRent = value, isSaved = false) }
    }

    fun updateElectricityUnitPrice(value: String) {
        _uiState.update { it.copy(electricityUnitPrice = value, isSaved = false) }
    }

    fun updateWaterUnitPrice(value: String) {
        _uiState.update { it.copy(waterUnitPrice = value, isSaved = false) }
    }

    fun updateInternetFee(value: String) {
        _uiState.update { it.copy(internetFee = value, isSaved = false) }
    }

    fun updateGarbageFee(value: String) {
        _uiState.update { it.copy(garbageFee = value, isSaved = false) }
    }

    fun updateExtraFee(value: String) {
        _uiState.update { it.copy(extraFee = value, isSaved = false) }
    }

    fun saveConfig() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val config = ConfigEntity(
                id = 1,
                roomRent = FormatUtils.parseFormattedDouble(currentState.roomRent),
                electricityUnitPrice = FormatUtils.parseFormattedDouble(currentState.electricityUnitPrice),
                waterUnitPrice = FormatUtils.parseFormattedDouble(currentState.waterUnitPrice),
                internetFee = FormatUtils.parseFormattedDouble(currentState.internetFee),
                garbageFee = FormatUtils.parseFormattedDouble(currentState.garbageFee),
                extraFee = FormatUtils.parseFormattedDouble(currentState.extraFee)
            )
            repository.saveConfig(config)
            _uiState.update {
                it.copy(
                    isSaved = true,
                    userMessage = "Đã lưu cấu hình đơn giá thành công!"
                )
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
