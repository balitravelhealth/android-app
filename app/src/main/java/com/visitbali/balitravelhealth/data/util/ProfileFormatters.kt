package com.visitbali.balitravelhealth.data.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object ProfileFormatters {
    private val apiDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val displayDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)

    fun toApiDate(value: String): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return ""

        val formatters = listOf(
            apiDateFormatter,
            displayDateFormatter,
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
        )

        for (formatter in formatters) {
            try {
                return LocalDate.parse(trimmed, formatter).format(apiDateFormatter)
            } catch (_: DateTimeParseException) {
                // Try the next known app date shape.
            }
        }

        return trimmed
    }

    fun toDisplayDate(value: String?): String {
        val trimmed = value?.trim().orEmpty()
        if (trimmed.isEmpty()) return ""

        return runCatching {
            LocalDate.parse(trimmed, apiDateFormatter).format(displayDateFormatter)
        }.getOrDefault(trimmed)
    }

    fun toApiGender(value: String): String {
        return when (value.trim().lowercase(Locale.US)) {
            "male", "man", "laki-laki", "laki_laki", "pria" -> "male"
            "female", "woman", "perempuan", "wanita" -> "female"
            else -> value.trim().lowercase(Locale.US)
        }
    }

    fun toDisplayGender(value: String?): String {
        return when (value?.trim()?.lowercase(Locale.US)) {
            "male", "laki-laki", "laki_laki", "pria" -> "Male"
            "female", "perempuan", "wanita" -> "Female"
            else -> value.orEmpty()
        }
    }
}
