package com.visitbali.balitravelhealth.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.dao.GuideItemDao
import com.visitbali.balitravelhealth.data.dao.NurseDao
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class AppContentSyncRepository(
    private val context: Context,
    private val api: BaliHealthApiService,
    private val guideItemDao: GuideItemDao,
    private val nurseDao: NurseDao,
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

            listOf(guides, nurses).forEach { task ->
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
