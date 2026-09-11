package com.example.roombill.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roombill.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<ConfigEntity?>

    @Query("SELECT * FROM configs WHERE id = 1 LIMIT 1")
    suspend fun getConfigOnce(): ConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: ConfigEntity)

    @Update
    suspend fun updateConfig(config: ConfigEntity)
}
