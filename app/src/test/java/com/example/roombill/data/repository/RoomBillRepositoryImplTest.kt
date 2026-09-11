package com.example.roombill.data.repository

import com.example.roombill.data.local.dao.BillDao
import com.example.roombill.data.local.dao.ConfigDao
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RoomBillRepositoryImplTest {

    private lateinit var fakeConfigDao: FakeConfigDao
    private lateinit var fakeBillDao: FakeBillDao
    private lateinit var repository: RoomBillRepositoryImpl

    @Before
    fun setUp() {
        fakeConfigDao = FakeConfigDao()
        fakeBillDao = FakeBillDao()
        repository = RoomBillRepositoryImpl(fakeConfigDao, fakeBillDao)
    }

    @Test
    fun saveAndGetConfig() = runTest {
        val config = ConfigEntity(
            id = 1,
            roomRent = 3000.0,
            electricityUnitPrice = 7.0,
            waterUnitPrice = 18.0,
            internetFee = 200.0,
            garbageFee = 50.0,
            extraFee = 0.0
        )

        repository.saveConfig(config)

        val retrievedOnce = repository.getConfigOnce()
        assertNotNull(retrievedOnce)
        assertEquals(3000.0, retrievedOnce?.roomRent ?: 0.0, 0.001)

        val retrievedFlow = repository.getConfig().first()
        assertEquals(7.0, retrievedFlow?.electricityUnitPrice ?: 0.0, 0.001)
    }

    @Test
    fun insertAndGetLatestBill() = runTest {
        val bill1 = BillEntity(
            id = 1L,
            month = 1,
            year = 2025,
            prevElectricityReading = 100.0,
            currElectricityReading = 150.0,
            electricityCost = 350.0,
            prevWaterReading = 50.0,
            currWaterReading = 60.0,
            waterCost = 180.0,
            roomRentCost = 3000.0,
            internetCost = 200.0,
            garbageCost = 50.0,
            extraCost = 0.0,
            totalAmount = 3780.0
        )

        val bill2 = BillEntity(
            id = 2L,
            month = 2,
            year = 2025,
            prevElectricityReading = 150.0,
            currElectricityReading = 210.0,
            electricityCost = 420.0,
            prevWaterReading = 60.0,
            currWaterReading = 72.0,
            waterCost = 216.0,
            roomRentCost = 3000.0,
            internetCost = 200.0,
            garbageCost = 50.0,
            extraCost = 0.0,
            totalAmount = 3886.0
        )

        repository.insertBill(bill1)
        repository.insertBill(bill2)

        val latest = repository.getLatestBill()
        assertNotNull(latest)
        assertEquals(2L, latest?.id)
        assertEquals(2, latest?.month)
        assertEquals(210.0, latest?.currElectricityReading ?: 0.0, 0.001)

        val allBills = repository.getAllBills().first()
        assertEquals(2, allBills.size)
    }

    @Test
    fun deleteBill() = runTest {
        val bill = BillEntity(
            id = 10L,
            month = 3,
            year = 2025,
            prevElectricityReading = 210.0,
            currElectricityReading = 260.0,
            electricityCost = 350.0,
            prevWaterReading = 72.0,
            currWaterReading = 80.0,
            waterCost = 144.0,
            roomRentCost = 3000.0,
            internetCost = 200.0,
            garbageCost = 50.0,
            extraCost = 0.0,
            totalAmount = 3744.0
        )

        repository.insertBill(bill)
        assertNotNull(repository.getBillByIdOnce(10L))

        repository.deleteBillById(10L)
        assertNull(repository.getBillByIdOnce(10L))
    }

    private class FakeConfigDao : ConfigDao {
        private val state = MutableStateFlow<ConfigEntity?>(null)

        override fun getConfig(): Flow<ConfigEntity?> = state

        override suspend fun getConfigOnce(): ConfigEntity? = state.value

        override suspend fun insertOrUpdateConfig(config: ConfigEntity) {
            state.value = config
        }

        override suspend fun updateConfig(config: ConfigEntity) {
            state.value = config
        }
    }

    private class FakeBillDao : BillDao {
        private val billsMap = mutableMapOf<Long, BillEntity>()
        private val state = MutableStateFlow<Map<Long, BillEntity>>(emptyMap())

        private fun notifyState() {
            state.value = billsMap.toMap()
        }

        override suspend fun insertBill(bill: BillEntity): Long {
            val id = if (bill.id == 0L) (billsMap.keys.maxOrNull() ?: 0L) + 1L else bill.id
            val newBill = bill.copy(id = id)
            billsMap[id] = newBill
            notifyState()
            return id
        }

        override suspend fun updateBill(bill: BillEntity) {
            billsMap[bill.id] = bill
            notifyState()
        }

        override suspend fun deleteBill(bill: BillEntity) {
            billsMap.remove(bill.id)
            notifyState()
        }

        override suspend fun deleteBillById(id: Long) {
            billsMap.remove(id)
            notifyState()
        }

        override fun getBillById(id: Long): Flow<BillEntity?> {
            return state.map { it[id] }
        }

        override suspend fun getBillByIdOnce(id: Long): BillEntity? {
            return billsMap[id]
        }

        override fun getAllBills(): Flow<List<BillEntity>> {
            return state.map { map ->
                map.values.sortedWith(
                    compareByDescending<BillEntity> { it.year }
                        .thenByDescending { it.month }
                        .thenByDescending { it.id }
                )
            }
        }

        override suspend fun getLatestBill(): BillEntity? {
            return billsMap.values.sortedWith(
                compareByDescending<BillEntity> { it.year }
                    .thenByDescending { it.month }
                    .thenByDescending { it.id }
            ).firstOrNull()
        }

        override fun getLatestBillFlow(): Flow<BillEntity?> {
            return state.map { map ->
                map.values.sortedWith(
                    compareByDescending<BillEntity> { it.year }
                        .thenByDescending { it.month }
                        .thenByDescending { it.id }
                ).firstOrNull()
            }
        }
    }
}
