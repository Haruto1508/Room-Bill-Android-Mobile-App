package com.example.roombill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roombill.data.local.entity.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Long)

    @Query("SELECT * FROM bills WHERE id = :id LIMIT 1")
    fun getBillById(id: Long): Flow<BillEntity?>

    @Query("SELECT * FROM bills WHERE id = :id LIMIT 1")
    suspend fun getBillByIdOnce(id: Long): BillEntity?

    @Query("SELECT * FROM bills ORDER BY year DESC, month DESC, id DESC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills ORDER BY year DESC, month DESC, id DESC LIMIT 1")
    suspend fun getLatestBill(): BillEntity?

    @Query("SELECT * FROM bills ORDER BY year DESC, month DESC, id DESC LIMIT 1")
    fun getLatestBillFlow(): Flow<BillEntity?>
}
