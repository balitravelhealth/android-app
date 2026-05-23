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
import com.visitbali.balitravelhealth.data.remote.ContentApiService
import com.visitbali.balitravelhealth.data.remote.NurseApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class AppContentSyncRepository(
    private val context: Context,
    private val contentApi: ContentApiService,
    private val nurseApi: NurseApiService,
    private val healthcareFacilityDao: HealthcareFacilityDao,
    private val guideItemDao: GuideItemDao,
    private val lifeSupportItemDao: LifeSupportItemDao,
    private val nurseDao: NurseDao
) {
    suspend fun refreshIfConnected(): Result<Unit> {
        return if (isConnected()) {
            refresh()
        } else {
            Result.success(Unit)
        }
    }

    suspend fun refresh(): Result<Unit> = runCatching {
        supervisorScope {
            val guides = async {
                contentApi.getEmergencyGuides().also { response ->
                    if (!response.success) error(response.message ?: "Failed to fetch emergency guides")
                    guideItemDao.replaceAll(response.data)
                }
            }

            val lifeSupport = async {
                contentApi.getBasicLifeSupport().also { response ->
                    if (!response.success) error(response.message ?: "Failed to fetch basic life support")
                    lifeSupportItemDao.replaceAll(response.data)
                }
            }

            val facilities = async {
                contentApi.getHealthcareFacilities().also { response ->
                    if (!response.success) error(response.message ?: "Failed to fetch healthcare facilities")
                    if (response.data.isNotEmpty()) {
                        healthcareFacilityDao.replaceAll(response.data)
                    }
                }
            }

            val nurses = async {
                nurseApi.listNurses(limit = 100, offset = 0, isActive = true).also { response ->
                    if (!response.success) error(response.message ?: "Failed to fetch nurses")
                    nurseDao.replaceAll(response.data)
                }
            }

            listOf(guides, lifeSupport, facilities, nurses).forEach { task ->
                runCatching { task.await() }
                    .onFailure { if (BuildConfig.DEBUG) Log.w("ContentSync", "A content sync task failed", it) }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
