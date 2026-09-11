package com.example.roombill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.repository.RoomBillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class BillHistoryUiState(
    val bills: List<BillEntity> = emptyList(),
    val filteredBills: List<BillEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedMonth: Int? = null,
    val selectedYear: Int? = null,
    val availableYears: List<Int> = emptyList(),
    val selectedBillForDetail: BillEntity? = null,
    val billToDelete: BillEntity? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class BillHistoryViewModel(
    private val repository: RoomBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillHistoryUiState())
    val uiState: StateFlow<BillHistoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedMonth = MutableStateFlow<Int?>(null)
    private val _selectedYear = MutableStateFlow<Int?>(null)

    init {
        loadBills()
    }

    private fun loadBills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                repository.getAllBills(),
                _searchQuery,
                _selectedMonth,
                _selectedYear
            ) { bills, query, month, year ->
                val availableYears = bills.map { it.year }.distinct().sortedDescending()
                val filtered = filterBills(bills, query, month, year)

                val validYear = if (year != null && year != 0 && year !in availableYears) null else year

                BillHistoryUiState(
                    bills = bills,
                    filteredBills = filtered,
                    searchQuery = query,
                    selectedMonth = month,
                    selectedYear = validYear,
                    availableYears = availableYears,
                    selectedBillForDetail = _uiState.value.selectedBillForDetail,
                    billToDelete = _uiState.value.billToDelete,
                    isLoading = false,
                    userMessage = _uiState.value.userMessage
                )
            }.collect { updatedState ->
                _uiState.value = updatedState
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSelectedMonth(month: Int?) {
        val normalized = if (month == 0) null else month
        _selectedMonth.value = normalized
        _uiState.update { it.copy(selectedMonth = normalized) }
    }

    fun updateSelectedYear(year: Int?) {
        val normalized = if (year == 0) null else year
        _selectedYear.value = normalized
        _uiState.update { it.copy(selectedYear = normalized) }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedMonth.value = null
        _selectedYear.value = null
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedMonth = null,
                selectedYear = null
            )
        }
    }

    fun selectBillForDetail(bill: BillEntity?) {
        _uiState.update { it.copy(selectedBillForDetail = bill) }
    }

    fun fetchBillById(id: Long) {
        viewModelScope.launch {
            val bill = repository.getBillByIdOnce(id)
            _uiState.update { it.copy(selectedBillForDetail = bill) }
        }
    }

    fun onDeleteBillRequested(bill: BillEntity) {
        _uiState.update { it.copy(billToDelete = bill) }
    }

    fun confirmDeleteBill() {
        viewModelScope.launch {
            val bill = _uiState.value.billToDelete
            if (bill != null) {
                repository.deleteBill(bill)
                _uiState.update {
                    it.copy(
                        billToDelete = null,
                        selectedBillForDetail = if (it.selectedBillForDetail?.id == bill.id) null else it.selectedBillForDetail,
                        userMessage = "Đã xóa hóa đơn Tháng ${bill.month}/${bill.year}"
                    )
                }
            }
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(billToDelete = null) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun filterBills(
        bills: List<BillEntity>,
        query: String,
        month: Int?,
        year: Int?
    ): List<BillEntity> {
        val trimmedQuery = query.trim().lowercase()
        return bills.filter { bill ->
            val matchesMonth = (month == null || month == 0 || bill.month == month)
            val matchesYear = (year == null || year == 0 || bill.year == year)

            val matchesQuery = if (trimmedQuery.isBlank()) {
                true
            } else {
                val monthStr = bill.month.toString()
                val monthPaddedStr = String.format(Locale.US, "%02d", bill.month)
                val yearStr = bill.year.toString()
                val monthYearStr = "$monthStr/$yearStr"
                val monthPaddedYearStr = "$monthPaddedStr/$yearStr"
                val thangStr = "tháng $monthStr"
                val thangPaddedStr = "tháng $monthPaddedStr"
                val desc = bill.extraCostDescription.lowercase()

                desc.contains(trimmedQuery) ||
                        monthStr == trimmedQuery ||
                        monthPaddedStr == trimmedQuery ||
                        yearStr.contains(trimmedQuery) ||
                        monthYearStr.contains(trimmedQuery) ||
                        monthPaddedYearStr.contains(trimmedQuery) ||
                        thangStr.lowercase().contains(trimmedQuery) ||
                        thangPaddedStr.lowercase().contains(trimmedQuery)
            }

            matchesMonth && matchesYear && matchesQuery
        }
    }
}
