package de.randombyte.lighthub.show.flows.colorchanger

class ColorCategoriesConfig(
    val warm: List<String> = emptyList(),
    val cold: List<String> = emptyList(),
    val strobe: String
) {
    val all = mapOf("warm" to warm, "cold" to cold, "strobe" to listOf(strobe))

    companion object {
        const val FILE_NAME = "color-categories"
    }
}