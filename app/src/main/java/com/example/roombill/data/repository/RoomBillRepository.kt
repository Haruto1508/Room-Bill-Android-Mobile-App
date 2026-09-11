package com.example.roombill.data.repository

import com.example.roombill.data.local.dao.BillDao
import com.example.roombill.data.local.dao.ConfigDao
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow

interface RoomBillRepository {
    fun getConfig(): Flow<ConfigEntity?>
    suspend fun getConfigOnce(): ConfigEntity?
    suspend fun saveConfig(config: ConfigEntity)

    fun getAllBills(): Flow<List<BillEntity>>
    fun getBillById(id: Long): Flow<BillEntity?>
    suspend fun getBillByIdOnce(id: Long): BillEntity?
    suspend fun getLatestBill(): BillEntity?
    fun getLatestBillFlow(): Flow<BillEntity?>

    suspend fun insertBill(bill: BillEntity): Long
    suspend fun updateBill(bill: BillEntity)
    suspend fun deleteBill(bill: BillEntity)
    suspend fun deleteBillById(id: Long)
}

class RoomBillRepositoryImpl(
    private val configDao: ConfigDao,
    private val billDao: BillDao
) : RoomBillRepository {

    override fun getConfig(): Flow<ConfigEntity?> = configDao.getConfig()

    override suspend fun getConfigOnce(): ConfigEntity? = configDao.getConfigOnce()

    override suspend fun saveConfig(config: ConfigEntity) {
        configDao.insertOrUpdateConfig(config)
    }

    override fun getAllBills(): Flow<List<BillEntity>> = billDao.getAllBills()

    override fun getBillById(id: Long): Flow<BillEntity?> = billDao.getBillById(id)

    override suspend fun getBillByIdOnce(id: Long): BillEntity? = billDao.getBillByIdOnce(id)

    override suspend fun getLatestBill(): BillEntity? = billDao.getLatestBill()

    override fun getLatestBillFlow(): Flow<BillEntity?> = billDao.getLatestBillFlow()

    override suspend fun insertBill(bill: BillEntity): Long = billDao.insertBill(bill)

    override suspend fun updateBill(bill: BillEntity) = billDao.updateBill(bill)

    override suspend fun deleteBill(bill: BillEntity) = billDao.deleteBill(bill)

    override suspend fun deleteBillById(id: Long) = billDao.deleteBillById(id)
}
