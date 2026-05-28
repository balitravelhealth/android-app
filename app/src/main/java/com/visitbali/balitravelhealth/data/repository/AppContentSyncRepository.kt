package com.visitbali.balitravelhealth.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.dao.GuideItemDao
import com.visitbali.balitravelhealth.data.dao.HealthcareFacilityDao
import com.visitbali.balitravelhealth.data.dao.LifeSupportItemDao
import com.visitbali.balitravelhealth.data.dao.NurseDao
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class AppContentSyncRepository(
    private val context: Context,
    private val api: BaliHealthApiService,
    private val guideItemDao: GuideItemDao,
    private val nurseDao: NurseDao,
    private val lifeSupportItemDao: LifeSupportItemDao,
    private val healthcareFacilityDao: HealthcareFacilityDao,
) {
    suspend fun refreshIfConnected(): Result<Unit> =
        if (isConnected()) refresh() else Result.success(Unit)

    suspend fun refresh(): Result<Unit> = runCatching {
        supervisorScope {
            val guides = async {
                val response = api.getEmergencyGuides()
                guideItemDao.replaceAll(response.data.map { it.toEntity() })
            }

            val nurses = async {
                val response = api.getNurses()
                nurseDao.replaceAll(response.data)
            }

            val blsFlows = async {
                val response = api.getEmergencyGuideFlows()
                lifeSupportItemDao.replaceAll(response.data.map { it.toEntity() })
            }

            val facilities = async {
                runCatching { api.getFacilities() }
                    .onSuccess { response ->
                        if (response.data.isNotEmpty()) {
                            healthcareFacilityDao.replaceAll(response.data)
                        }
                    }
                    .onFailure {
                        if (BuildConfig.DEBUG) Log.w("ContentSync", "Failed to fetch facilities, ensuring seeder data exists", it)
                        if (healthcareFacilityDao.count() == 0) {
                            healthcareFacilityDao.insertAll(com.visitbali.balitravelhealth.data.database.DatabaseSeeder.getAllFacilities())
                        }
                    }
            }

            listOf(guides, nurses, blsFlows, facilities).forEach { task ->
                runCatching { task.await() }
                    .onFailure {
                        if (BuildConfig.DEBUG) Log.w("ContentSync", "Sync task failed", it)
                    }
            }
        }
    }

    private fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
