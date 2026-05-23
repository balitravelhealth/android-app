package com.visitbali.balitravelhealth.data.database

import androidx.room.TypeConverter
import com.visitbali.balitravelhealth.data.model.FacilityType
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun fromFacilityType(type: FacilityType): String = type.name

    @TypeConverter
    fun toFacilityType(value: String): FacilityType = FacilityType.valueOf(value)

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String = value.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal = value.toBigDecimal()
}
