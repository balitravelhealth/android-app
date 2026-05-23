package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dao.LifeSupportItemDao
import com.visitbali.balitravelhealth.data.model.LifeSupportItem
import kotlinx.coroutines.flow.Flow

class LifeSupportRepository(
    private val dao: LifeSupportItemDao
) {
    val items: Flow<List<LifeSupportItem>> = dao.getAll()
}
