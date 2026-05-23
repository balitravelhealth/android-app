package com.visitbali.balitravelhealth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.visitbali.balitravelhealth.data.model.LifeSupportItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeSupportItemDao {
    @Query("SELECT * FROM life_support_items ORDER BY sortOrder, title")
    fun getAll(): Flow<List<LifeSupportItem>>

    @Query("SELECT * FROM life_support_items ORDER BY sortOrder, title")
    suspend fun getAllSnapshot(): List<LifeSupportItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LifeSupportItem>)

    @Query("DELETE FROM life_support_items")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<LifeSupportItem>) {
        clear()
        insertAll(items)
    }
}
