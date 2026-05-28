package com.visitbali.balitravelhealth.data.model

enum class GuideIcon {
    Heart,
    Wind,
    Flame,
    Bandage,
    Sun,
    Insect,
    WaterDrop,
    Fall,
}

data class EmergencyGuide(
    val id: String,
    val title: String,
    val summary: String,
    val icon: GuideIcon,
    val imageName: String? = null,
)

object EmergencyGuideCatalog {
    val guides = listOf(
        EmergencyGuide("cpr", "CPR Basics", "How to perform chest compressions and rescue breaths.", GuideIcon.Heart),
        EmergencyGuide("choking", "Choking Relief", "Steps for the abdominal thrust (Heimlich manoeuvre).", GuideIcon.Wind),
        EmergencyGuide("burns", "Treating Burns", "First aid for minor and severe burns.", GuideIcon.Flame),
        EmergencyGuide("bleeding", "Bleeding Control", "How to stop heavy bleeding and dress a wound.", GuideIcon.Bandage),
        EmergencyGuide("heat", "Heat Stroke", "Recognise the signs and cool a casualty quickly.", GuideIcon.Sun),
        EmergencyGuide("snake", "Snake & Insect Bites", "What to do if bitten or stung in Bali.", GuideIcon.Insect),
        EmergencyGuide("drowning", "Drowning Response", "Rescue, recovery position, and aftercare.", GuideIcon.WaterDrop),
        EmergencyGuide("fracture", "Fractures & Sprains", "Splinting and immobilising a limb safely.", GuideIcon.Fall),
    )

    fun find(id: String): EmergencyGuide? = guides.firstOrNull { it.id == id }
}
