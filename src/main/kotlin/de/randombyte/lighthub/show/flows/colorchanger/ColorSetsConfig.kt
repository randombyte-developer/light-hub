package de.randombyte.lighthub.show.flows.colorchanger

class ColorSetsConfig(
    val `set-1`: List<String> = emptyList(),
    val `set-2`: List<String> = emptyList(),
    val `set-3`: List<String> = emptyList(),
    val `set-4`: List<String> = emptyList(),
    val `set-5`: List<String> = emptyList(),
    val `set-6`: List<String> = emptyList(),
    val `set-7`: List<String> = emptyList(),
    val `set-8`: List<String> = emptyList(),
    val strobe: String = ""
) {
    val all = mapOf(
        "set-1" to `set-1`,
        "set-2" to `set-2`,
        "set-3" to `set-3`,
        "set-4" to `set-4`,
        "set-5" to `set-5`,
        "set-6" to `set-6`,
        "set-7" to `set-7`,
        "set-8" to `set-8`,
        "strobe" to listOf(strobe)
    )

    companion object {
        const val FILE_NAME = "color-sets"
    }
}