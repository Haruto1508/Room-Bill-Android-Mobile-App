package com.example.roombill.ui.viewmodel

import com.example.roombill.MainDispatcherRule
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.repository.FakeRoomBillRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BillHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeRoomBillRepository
    private lateinit var viewModel: BillHistoryViewModel

    private val bill1 = BillEntity(
        id = 1L,
        month = 1,
        year = 2025,
        prevElectricityReading = 100.0,
        currElectricityReading = 150.0,
        electricityCost = 175000.0,
        prevWaterReading = 50.0,
        currWaterReading = 60.0,
        waterCost = 200000.0,
        roomRentCost = 3000000.0,
        internetCost = 100000.0,
        garbageCost = 30000.0,
        extraCost = 0.0,
        extraCostDescription = "",
        totalAmount = 3505000.0
    )

    private val bill2 = BillEntity(
        id = 2L,
        month = 2,
        year = 2025,
        prevElectricityReading = 150.0,
        currElectricityReading = 210.0,
        electricityCost = 210000.0,
        prevWaterReading = 60.0,
        currWaterReading = 72.0,
        waterCost = 240000.0,
        roomRentCost = 3000000.0,
        internetCost = 100000.0,
        garbageCost = 30000.0,
        extraCost = 50000.0,
        extraCostDescription = "Sửa bản lề cửa",
        totalAmount = 3630000.0
    )

    private val bill3 = BillEntity(
        id = 3L,
        month = 12,
        year = 2024,
        prevElectricityReading = 80.0,
        currElectricityReading = 100.0,
        electricityCost = 70000.0,
        prevWaterReading = 40.0,
        currWaterReading = 50.0,
        waterCost = 200000.0,
        roomRentCost = 3000000.0,
        internetCost = 100000.0,
        garbageCost = 30000.0,
        extraCost = 0.0,
        extraCostDescription = "",
        totalAmount = 3400000.0
    )

    @Before
    fun setUp() = runTest {
        repository = FakeRoomBillRepository()
        repository.insertBill(bill1)
        repository.insertBill(bill2)
        repository.insertBill(bill3)
        viewModel = BillHistoryViewModel(repository)
    }

    @Test
    fun loadBills_exposesAllBillsFromRepositorySorted() = runTest {
        val state = viewModel.uiState.value
        assertEquals(3, state.bills.size)
        assertEquals(3, state.filteredBills.size)
        // Order expected: 2025 Month 2 (bill2), 2025 Month 1 (bill1), 2024 Month 12 (bill3)
        assertEquals(2L, state.bills[0].id)
        assertEquals(1L, state.bills[1].id)
        assertEquals(3L, state.bills[2].id)

        assertEquals(listOf(2025, 2024), state.availableYears)
    }

    @Test
    fun filterBills_bySearchQuery_matchesDescriptionOrMonthYear() = runTest {
        // Search by description "Sửa"
        viewModel.updateSearchQuery("Sửa")
        var state = viewModel.uiState.value
        assertEquals(1, state.filteredBills.size)
        assertEquals(2L, state.filteredBills[0].id)

        // Search by year "2024"
        viewModel.updateSearchQuery("2024")
        state = viewModel.uiState.value
        assertEquals(1, state.filteredBills.size)
        assertEquals(3L, state.filteredBills[0].id)

        // Search by month "12"
        viewModel.updateSearchQuery("12")
        state = viewModel.uiState.value
        assertEquals(1, state.filteredBills.size)
        assertEquals(3L, state.filteredBills[0].id)
    }

    @Test
    fun filterBills_bySelectedMonthAndYear() = runTest {
        viewModel.updateSelectedYear(2025)
        var state = viewModel.uiState.value
        assertEquals(2, state.filteredBills.size)

        viewModel.updateSelectedMonth(2)
        state = viewModel.uiState.value
        assertEquals(1, state.filteredBills.size)
        assertEquals(2L, state.filteredBills[0].id)
    }

    @Test
    fun clearFilters_resetsQueryAndFilters() = runTest {
        viewModel.updateSearchQuery("Sửa")
        viewModel.updateSelectedMonth(2)
        viewModel.updateSelectedYear(2025)

        viewModel.clearFilters()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.selectedMonth)
        assertNull(state.selectedYear)
        assertEquals(3, state.filteredBills.size)
    }

    @Test
    fun selectBillForDetail_and_fetchBillById_setsSelectedBill() = runTest {
        viewModel.selectBillForDetail(bill2)
        assertEquals(bill2, viewModel.uiState.value.selectedBillForDetail)

        viewModel.fetchBillById(1L)
        assertEquals(1L, viewModel.uiState.value.selectedBillForDetail?.id)
    }

    @Test
    fun deleteBill_confirmationFlow_removesBillFromRepository() = runTest {
        viewModel.onDeleteBillRequested(bill1)
        assertEquals(bill1, viewModel.uiState.value.billToDelete)

        viewModel.confirmDeleteBill()

        val state = viewModel.uiState.value
        assertNull(state.billToDelete)
        assertEquals(2, state.bills.size)
        assertNull(repository.getBillByIdOnce(1L))
        assertNotNull(state.userMessage)
    }

    @Test
    fun cancelDelete_resetsBillToDelete() = runTest {
        viewModel.onDeleteBillRequested(bill1)
        assertEquals(bill1, viewModel.uiState.value.billToDelete)

        viewModel.cancelDelete()

        val state = viewModel.uiState.value
        assertNull(state.billToDelete)
        assertEquals(3, state.bills.size)
    }
}
