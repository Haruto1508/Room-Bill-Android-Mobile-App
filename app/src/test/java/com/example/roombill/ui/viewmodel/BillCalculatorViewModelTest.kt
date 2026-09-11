package com.example.roombill.ui.viewmodel

import com.example.roombill.MainDispatcherRule
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.local.entity.ConfigEntity
import com.example.roombill.data.repository.FakeRoomBillRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BillCalculatorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeRoomBillRepository
    private lateinit var viewModel: BillCalculatorViewModel

    @Before
    fun setUp() {
        repository = FakeRoomBillRepository()
    }

    @Test
    fun initialLoad_autoPopulatesFromLatestBillAndConfig() = runTest {
        val config = ConfigEntity(
            id = 1,
            roomRent = 3000000.0,
            electricityUnitPrice = 3500.0,
            waterUnitPrice = 20000.0,
            internetFee = 100000.0,
            garbageFee = 30000.0,
            extraFee = 0.0
        )
        repository.saveConfig(config)

        val previousBill = BillEntity(
            id = 1L,
            month = 2,
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
            totalAmount = 3505000.0
        )
        repository.insertBill(previousBill)

        viewModel = BillCalculatorViewModel(repository)

        val state = viewModel.uiState.value
        assertEquals(3, state.month)
        assertEquals(2025, state.year)
        assertEquals("150", state.prevElectricityReading)
        assertEquals("60", state.prevWaterReading)
        assertEquals("3000000", state.roomRentCost)
        assertEquals("100000", state.internetCost)
        assertEquals("30000", state.garbageCost)
        assertTrue(state.isAutoPopulated)
    }

    @Test
    fun calculations_computeSubtotalsAndTotalCorrectly() = runTest {
        val config = ConfigEntity(
            id = 1,
            roomRent = 3000000.0,
            electricityUnitPrice = 3500.0,
            waterUnitPrice = 20000.0,
            internetFee = 100000.0,
            garbageFee = 30000.0,
            extraFee = 0.0
        )
        repository.saveConfig(config)

        viewModel = BillCalculatorViewModel(repository)

        viewModel.updatePrevElectricityReading("150")
        viewModel.updateCurrElectricityReading("200") // usage = 50 kWh -> 175,000 VND
        viewModel.updatePrevWaterReading("60")
        viewModel.updateCurrWaterReading("70") // usage = 10 m³ -> 200,000 VND
        viewModel.updateExtraCost("50000")
        viewModel.updateExtraCostDescription("Phí sửa chữa")

        val state = viewModel.uiState.value
        assertEquals(50.0, state.electricityUsage, 0.01)
        assertEquals(175000.0, state.electricityCost, 0.01)
        assertEquals(10.0, state.waterUsage, 0.01)
        assertEquals(200000.0, state.waterCost, 0.01)

        // Total = 3,000,000 + 175,000 + 200,000 + 100,000 + 30,000 + 50,000 = 3,555,000 VND
        assertEquals(3555000.0, state.totalAmount, 0.01)
    }

    @Test
    fun saveBill_insertsNewBillToRepository() = runTest {
        val config = ConfigEntity(
            id = 1,
            roomRent = 3000000.0,
            electricityUnitPrice = 3500.0,
            waterUnitPrice = 20000.0,
            internetFee = 100000.0,
            garbageFee = 30000.0,
            extraFee = 0.0
        )
        repository.saveConfig(config)

        viewModel = BillCalculatorViewModel(repository)

        viewModel.updateMonth(3)
        viewModel.updateYear(2025)
        viewModel.updatePrevElectricityReading("100")
        viewModel.updateCurrElectricityReading("150")
        viewModel.updatePrevWaterReading("50")
        viewModel.updateCurrWaterReading("60")

        viewModel.saveBill()

        val savedBill = repository.getLatestBill()
        assertNotNull(savedBill)
        assertEquals(3, savedBill?.month)
        assertEquals(2025, savedBill?.year)
        assertEquals(150.0, savedBill?.currElectricityReading ?: 0.0, 0.01)
        assertEquals(60.0, savedBill?.currWaterReading ?: 0.0, 0.01)
        assertEquals(3505000.0, savedBill?.totalAmount ?: 0.0, 0.01)

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertNotNull(state.userMessage)
    }
}
