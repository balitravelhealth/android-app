package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dao.GuideItemDao
import com.visitbali.balitravelhealth.data.model.GuideItem
import kotlinx.coroutines.flow.Flow

class GuideRepository(
    private val dao: GuideItemDao
) {
    val guides: Flow<List<GuideItem>> = dao.getAll()
}
