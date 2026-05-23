package com.visitbali.balitravelhealth.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.visitbali.balitravelhealth.data.dao.HealthcareFacilityDao
import com.visitbali.balitravelhealth.data.dao.GuideItemDao
import com.visitbali.balitravelhealth.data.dao.LifeSupportItemDao
import com.visitbali.balitravelhealth.data.dao.NurseDao
import com.visitbali.balitravelhealth.data.model.GuideItem
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import com.visitbali.balitravelhealth.data.model.LifeSupportItem
import com.visitbali.balitravelhealth.data.model.Nurse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        HealthcareFacility::class,
        Nurse::class,
        GuideItem::class,
        LifeSupportItem::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun healthcareFacilityDao(): HealthcareFacilityDao
    abstract fun nurseDao(): NurseDao
    abstract fun guideItemDao(): GuideItemDao
    abstract fun lifeSupportItemDao(): LifeSupportItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration 1 → 2: adds four operating-hours columns.
         * All columns are nullable so existing rows won't break.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE healthcare_facilities ADD COLUMN isOpen24Hours INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE healthcare_facilities ADD COLUMN outpatientHours TEXT")
                db.execSQL("ALTER TABLE healthcare_facilities ADD COLUMN emergencyHours TEXT")
                db.execSQL("ALTER TABLE healthcare_facilities ADD COLUMN hoursSummary TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS nurses (
                        id TEXT NOT NULL PRIMARY KEY,
                        fullName TEXT NOT NULL,
                        yearsOfExperience INTEGER NOT NULL,
                        ratePerAppointment TEXT NOT NULL,
                        specialization TEXT,
                        bio TEXT,
                        profilePhotoUrl TEXT,
                        isActive INTEGER NOT NULL,
                        createdAt TEXT NOT NULL,
                        updatedAt TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS guide_items (
                        id TEXT NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        imageUrl TEXT,
                        content TEXT,
                        sortOrder INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS life_support_items (
                        id TEXT NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        description TEXT,
                        imageUrl TEXT,
                        content TEXT,
                        sortOrder INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bali_healthcare.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Pre-populates DB with all 17 BMTA facilities on first install.
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.healthcareFacilityDao().insertAll(
                        DatabaseSeeder.getAllFacilities()
                    )
                }
            }
        }
    }
}
