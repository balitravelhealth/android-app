package com.visitbali.balitravelhealth.data.repository

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.visitbali.balitravelhealth.data.dao.GuideItemDao
import com.visitbali.balitravelhealth.data.dto.EmergencyGuideItemDto
import com.visitbali.balitravelhealth.data.model.GuideCategory
import com.visitbali.balitravelhealth.data.model.GuideMenuContent
import com.visitbali.balitravelhealth.data.model.GuideItem
import com.visitbali.balitravelhealth.data.model.GuideStep
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import kotlinx.coroutines.flow.Flow

class GuideRepository(
    private val dao: GuideItemDao,
    private val api: BaliHealthApiService,
) {
    val guides: Flow<List<GuideItem>> = dao.getAll()

    suspend fun loadMenuContent(): Result<GuideMenuContent> {
        val flowsResult = runCatching { api.getEmergencyGuideFlows().data }
        val guidesResult = runCatching { api.getEmergencyGuides().data }

        val guideItems = guidesResult
            .onSuccess { remoteGuides -> dao.replaceAll(remoteGuides.map { it.toEntity() }) }
            .getOrElse {
                dao.getAllSnapshot().map { it.toDtoLike() }
            }

        val content = GuideMenuContent(
            flows = flowsResult.getOrDefault(emptyList()),
            categories = guideItems.toCategories(),
        )

        val error = flowsResult.exceptionOrNull() ?: guidesResult.exceptionOrNull()
        return if (content.flows.isNotEmpty() || content.categories.isNotEmpty()) {
            Result.success(content)
        } else {
            Result.failure(error ?: IllegalStateException("No guide content is available."))
        }
    }

    private fun GuideItem.toDtoLike(): EmergencyGuideItemDto = EmergencyGuideItemDto(
        id = id,
        kategori = kategori,
        langkah = langkah,
        isiMedia = null,
        createdAt = createdAt,
        updatedAt = updatedAt,
    ).copyWithCachedText(
        text = isiMediaTeks,
        imageUrl = isiMediaGambarUrl,
    )

    private fun EmergencyGuideItemDto.copyWithCachedText(
        text: String?,
        imageUrl: String?,
    ): EmergencyGuideItemDto {
        val media = JsonObject().apply {
            text?.let { addProperty("teks", it) }
            imageUrl?.let { addProperty("image_url", it) }
        }
        return copy(isiMedia = media)
    }

    private fun List<EmergencyGuideItemDto>.toCategories(): List<GuideCategory> {
        return groupBy { it.kategori }
            .map { (category, items) ->
                val sortedSteps = items.sortedBy { it.langkah }
                val first = sortedSteps.firstOrNull()
                val steps = sortedSteps.map { item ->
                    GuideStep(
                        id = item.id,
                        number = item.langkah,
                        title = item.mediaTitle() ?: "Step ${item.langkah}",
                        body = item.mediaBody() ?: item.mediaText().orEmpty(),
                        imageUrl = item.mediaImageUrl(),
                        iconName = item.mediaIconName(),
                    )
                }
                GuideCategory(
                    id = category,
                    title = category.readableCategoryName(),
                    summary = first?.mediaBody()
                        ?: first?.mediaText()
                        ?: "${steps.size} emergency steps",
                    steps = steps,
                    imageUrl = first?.mediaImageUrl(),
                    iconName = first?.mediaIconName(),
                )
            }
            .sortedWith(compareBy<GuideCategory> { it.id.categoryPriority() }.thenBy { it.title })
    }

    private fun String.categoryPriority(): Int = when {
        equals("CEK_NAPAS", ignoreCase = true) -> 0
        contains("CPR", ignoreCase = true) -> 1
        contains("AED", ignoreCase = true) -> 2
        contains("TERSEDAK", ignoreCase = true) -> 3
        else -> 10
    }

    private fun String.readableCategoryName(): String {
        return when (uppercase()) {
            "CEK_NAPAS" -> "Check Response & Breathing"
            "CPR_DEWASA" -> "Adult CPR"
            "CPR_ANAK" -> "Child & Infant CPR"
            "AED" -> "AED Guide"
            "TERSEDAK_DEWASA" -> "Adult Choking"
            "TERSEDAK_ANAK" -> "Child & Infant Choking"
            "ACCIDENTAL_INGESTION" -> "Accidental Ingestion"
            "LUKA" -> "Wound Care"
            "ALERGI" -> "Allergic Reaction"
            "DARURAT" -> "Emergency Numbers"
            else -> lowercase()
                .split("_", "-", " ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase() } }
        }
    }

    private fun EmergencyGuideItemDto.mediaTitle(): String? =
        mediaObject()?.firstString("judul", "title")

    private fun EmergencyGuideItemDto.mediaBody(): String? =
        mediaObject()?.firstString("teks", "body", "text", "description", "deskripsi")

    private fun EmergencyGuideItemDto.mediaImageUrl(): String? =
        mediaObject()?.firstString("gambar_url", "image_url", "imageUrl")

    private fun EmergencyGuideItemDto.mediaIconName(): String? =
        mediaObject()?.firstString("ikon", "icon", "icon_name")

    private fun EmergencyGuideItemDto.mediaText(): String? {
        val media = isiMedia ?: return null
        return when {
            media.isJsonNull -> null
            media.isJsonPrimitive -> media.asString
            media.isJsonObject -> listOfNotNull(mediaTitle(), mediaBody())
                .filter { it.isNotBlank() }
                .joinToString("\n\n")
                .ifBlank { null }
            else -> media.toString()
        }
    }

    private fun EmergencyGuideItemDto.mediaObject(): JsonObject? =
        isiMedia?.takeIf(JsonElement::isJsonObject)?.asJsonObject

    private fun JsonObject.firstString(vararg names: String): String? =
        names.firstNotNullOfOrNull { name ->
            get(name)?.takeIf { it.isJsonPrimitive }?.asString?.takeIf { value -> value.isNotBlank() }
        }
}
