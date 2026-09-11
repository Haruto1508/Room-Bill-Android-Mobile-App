package com.example.roombill.data.repository

import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRoomBillRepository : RoomBillRepository {
    private val configState = MutableStateFlow<ConfigEntity?>(null)
    private val billsMap = mutableMapOf<Long, BillEntity>()
    private val billsState = MutableStateFlow<Map<Long, BillEntity>>(emptyMap())

    private fun notifyBills() {
        billsState.value = billsMap.toMap()
    }

    override fun getConfig(): Flow<ConfigEntity?> = configState

    override suspend fun getConfigOnce(): ConfigEntity? = configState.value

    override suspend fun saveConfig(config: ConfigEntity) {
        configState.value = config
    }

    override fun getAllBills(): Flow<List<BillEntity>> {
        return billsState.map { map ->
            map.values.sortedWith(
                compareByDescending<BillEntity> { it.year }
                    .thenByDescending { it.month }
                    .thenByDescending { it.id }
            )
        }
    }

    override fun getBillById(id: Long): Flow<BillEntity?> {
        return billsState.map { it[id] }
    }

    override suspend fun getBillByIdOnce(id: Long): BillEntity? {
        return billsMap[id]
    }

    override suspend fun getLatestBill(): BillEntity? {
        return billsMap.values.sortedWith(
            compareByDescending<BillEntity> { it.year }
                .thenByDescending { it.month }
                .thenByDescending { it.id }
        ).firstOrNull()
    }

    override fun getLatestBillFlow(): Flow<BillEntity?> {
        return billsState.map { map ->
            map.values.sortedWith(
                compareByDescending<BillEntity> { it.year }
                    .thenByDescending { it.month }
                    .thenByDescending { it.id }
            ).firstOrNull()
        }
    }

    override suspend fun insertBill(bill: BillEntity): Long {
        val id = if (bill.id == 0L) (billsMap.keys.maxOrNull() ?: 0L) + 1L else bill.id
        val newBill = bill.copy(id = id)
        billsMap[id] = newBill
        notifyBills()
        return id
    }

    override suspend fun updateBill(bill: BillEntity) {
        billsMap[bill.id] = bill
        notifyBills()
    }

    override suspend fun deleteBill(bill: BillEntity) {
        billsMap.remove(bill.id)
        notifyBills()
    }

    override suspend fun deleteBillById(id: Long) {
        billsMap.remove(id)
        notifyBills()
    }
}
