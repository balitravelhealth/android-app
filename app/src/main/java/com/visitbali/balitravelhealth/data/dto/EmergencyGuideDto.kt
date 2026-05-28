package com.visitbali.balitravelhealth.data.dto

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.visitbali.balitravelhealth.data.model.GuideItem

data class IsiMediaDto(
    val teks: String? = null,
    val title: String? = null,
    val body: String? = null,
    @SerializedName("gambar_url") val gambarUrl: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
)

data class EmergencyGuideItemDto(
    val id: Int,
    val kategori: String,
    val langkah: Int,
    @SerializedName("isi_media") val isiMedia: JsonElement? = null,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
) {
    fun toEntity(): GuideItem = GuideItem(
        id = id,
        kategori = kategori,
        langkah = langkah,
        isiMediaTeks = parseMediaText(isiMedia),
        isiMediaGambarUrl = parseMediaImageUrl(isiMedia),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun parseMediaText(media: JsonElement?): String? {
        return when {
            media == null || media.isJsonNull -> null
            media.isJsonPrimitive -> media.asString
            media.isJsonObject -> media.asJsonObject.mediaText()
            else -> media.toString()
        }
    }

    private fun parseMediaImageUrl(media: JsonElement?): String? {
        return media?.takeIf { it.isJsonObject }?.asJsonObject?.firstString(
            "gambar_url",
            "image_url",
            "imageUrl",
        )
    }

    private fun JsonObject.mediaText(): String? {
        val title = firstString("title", "judul")
        val body = firstString("body", "teks", "text", "description", "deskripsi")
        return listOfNotNull(title, body)
            .filter { it.isNotBlank() }
            .joinToString("\n\n")
            .ifBlank { null }
    }

    private fun JsonObject.firstString(vararg names: String): String? {
        return names.firstNotNullOfOrNull { name ->
            get(name)?.takeIf { it.isJsonPrimitive }?.asString
        }
    }
}

data class EmergencyGuidesResponse(
    val data: List<EmergencyGuideItemDto> = emptyList(),
)

data class EmergencyGuideFlowSummary(
    val id: Int,
    val title: String,
    val kategori: String,
    val deskripsi: String? = "",
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
) {
    fun toEntity(): com.visitbali.balitravelhealth.data.model.LifeSupportItem = 
        com.visitbali.balitravelhealth.data.model.LifeSupportItem(
            id = id,
            title = title,
            kategori = kategori,
            deskripsi = deskripsi,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}

data class EmergencyGuideFlowsResponse(
    val data: List<EmergencyGuideFlowSummary> = emptyList(),
)

data class FlowChoice(
    val label: String,
    @SerializedName("next_id") val nextId: String?,
    val variant: String? = "neutral", // "yes", "no", "neutral"
)

data class FlowNode(
    val id: String,
    val title: String,
    val instruction: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_entry") val isEntry: Boolean,
    val choices: List<FlowChoice>,
)

data class EmergencyGuideFlowDetail(
    val id: Int,
    val title: String,
    val kategori: String,
    val deskripsi: String? = "",
    val nodes: List<FlowNode>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)
