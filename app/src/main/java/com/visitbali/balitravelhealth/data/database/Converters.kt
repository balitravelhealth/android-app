package com.visitbali.balitravelhealth.data.database

import androidx.room.TypeConverter
import com.visitbali.balitravelhealth.data.model.FacilityType

class Converters {
    @TypeConverter
    fun fromFacilityType(type: FacilityType): String = type.name

    @TypeConverter
    fun toFacilityType(value: String): FacilityType = FacilityType.valueOf(value)
}
