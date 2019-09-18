package de.randombyte.lighthub.show.flows.colorchanger

class ColorCategoriesConfig(
    val warm: List<String> = emptyList(),
    val cold: List<String> = emptyList()
) {
    val all = mapOf("warm" to warm, "cold" to cold)

    companion object {
        const val FILE_NAME = "color-categories"
    }
}