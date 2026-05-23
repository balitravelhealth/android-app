package com.visitbali.balitravelhealth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.visitbali.balitravelhealth.data.model.Nurse
import kotlinx.coroutines.flow.Flow

@Dao
interface NurseDao {
    @Query("SELECT * FROM nurses WHERE isActive = 1 ORDER BY fullName")
    fun getActiveNurses(): Flow<List<Nurse>>

    @Query("SELECT * FROM nurses WHERE isActive = 1 ORDER BY fullName")
    suspend fun getActiveNursesSnapshot(): List<Nurse>

    @Query("SELECT * FROM nurses WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Nurse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nurses: List<Nurse>)

    @Query("DELETE FROM nurses")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(nurses: List<Nurse>) {
        clear()
        insertAll(nurses)
    }
}
