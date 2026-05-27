package com.visitbali.balitravelhealth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.visitbali.balitravelhealth.data.model.GuideItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideItemDao {
    @Query("SELECT * FROM guide_items ORDER BY kategori, langkah")
    fun getAll(): Flow<List<GuideItem>>

    @Query("SELECT * FROM guide_items WHERE kategori = :kategori ORDER BY langkah")
    fun getByKategori(kategori: String): Flow<List<GuideItem>>

    @Query("SELECT * FROM guide_items ORDER BY kategori, langkah")
    suspend fun getAllSnapshot(): List<GuideItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GuideItem>)

    @Query("DELETE FROM guide_items")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<GuideItem>) {
        clear()
        insertAll(items)
    }
}
