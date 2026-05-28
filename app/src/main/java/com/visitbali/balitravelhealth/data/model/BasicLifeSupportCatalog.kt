package com.visitbali.balitravelhealth.data.model

enum class BlsIcon {
    Heart,
    Wind,
    Bandage,
    Flame,
    Fall,
    Shock,
}

data class BasicLifeSupportItem(
    val id: String,
    val title: String,
    val icon: BlsIcon,
)

object BasicLifeSupportCatalog {
    val items = listOf(
        BasicLifeSupportItem("cpr", "CPR", BlsIcon.Heart),
        BasicLifeSupportItem("choking", "Choking Relief", BlsIcon.Wind),
        BasicLifeSupportItem("bleeding", "Bleeding Control", BlsIcon.Bandage),
        BasicLifeSupportItem("burns", "Burns", BlsIcon.Flame),
        BasicLifeSupportItem("fracture", "Fractures", BlsIcon.Fall),
        BasicLifeSupportItem("shock", "Shock", BlsIcon.Shock),
    )

    fun find(id: String): BasicLifeSupportItem? = items.firstOrNull { it.id == id }
}
