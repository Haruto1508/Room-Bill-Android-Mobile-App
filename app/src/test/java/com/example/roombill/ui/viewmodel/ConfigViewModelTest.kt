package com.example.roombill.ui.viewmodel

import com.example.roombill.MainDispatcherRule
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
class ConfigViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeRoomBillRepository
    private lateinit var viewModel: ConfigViewModel

    @Before
    fun setUp() {
        repository = FakeRoomBillRepository()
    }

    @Test
    fun loadConfig_populatesUiStateWhenConfigExists() = runTest {
        val config = ConfigEntity(
            id = 1,
            roomRent = 3500000.0,
            electricityUnitPrice = 3800.0,
            waterUnitPrice = 18000.0,
            internetFee = 150000.0,
            garbageFee = 40000.0,
            extraFee = 10000.0
        )
        repository.saveConfig(config)

        viewModel = ConfigViewModel(repository)

        val state = viewModel.uiState.value
        assertEquals("3500000", state.roomRent)
        assertEquals("3800", state.electricityUnitPrice)
        assertEquals("18000", state.waterUnitPrice)
        assertEquals("150000", state.internetFee)
        assertEquals("40000", state.garbageFee)
        assertEquals("10000", state.extraFee)
    }

    @Test
    fun updateFields_updatesStateCorrectly() = runTest {
        viewModel = ConfigViewModel(repository)

        viewModel.updateRoomRent("4000000")
        viewModel.updateElectricityUnitPrice("4000")
        viewModel.updateWaterUnitPrice("20000")
        viewModel.updateInternetFee("200000")
        viewModel.updateGarbageFee("50000")
        viewModel.updateExtraFee("20000")

        val state = viewModel.uiState.value
        assertEquals("4000000", state.roomRent)
        assertEquals("4000", state.electricityUnitPrice)
        assertEquals("20000", state.waterUnitPrice)
        assertEquals("200000", state.internetFee)
        assertEquals("50000", state.garbageFee)
        assertEquals("20000", state.extraFee)
    }

    @Test
    fun saveConfig_persistsInRepository() = runTest {
        viewModel = ConfigViewModel(repository)

        viewModel.updateRoomRent("3000000")
        viewModel.updateElectricityUnitPrice("3500")
        viewModel.updateWaterUnitPrice("15000")
        viewModel.updateInternetFee("100000")
        viewModel.updateGarbageFee("30000")
        viewModel.updateExtraFee("0")

        viewModel.saveConfig()

        val savedConfig = repository.getConfigOnce()
        assertNotNull(savedConfig)
        assertEquals(3000000.0, savedConfig?.roomRent ?: 0.0, 0.01)
        assertEquals(3500.0, savedConfig?.electricityUnitPrice ?: 0.0, 0.01)

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertNotNull(state.userMessage)
    }
}
