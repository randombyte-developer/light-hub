package de.randombyte.lighthub.osc.devices.features.colors

sealed class QuadPhaseColor(id: String, dmxRange: IntRange) : FixedColor(id, dmxRange) {

    object Off : QuadPhaseColor("off", -1..-1)
    object Red : QuadPhaseColor("r", 0..16)
    object Green : QuadPhaseColor("g", 17..33)
    object Blue : QuadPhaseColor("b", 34..50)
    object White : QuadPhaseColor("w", 51..67)
    object RedGreen : QuadPhaseColor("rg", 68..84)
    object RedBlue : QuadPhaseColor("rb", 85..101)
    object RedWhite : QuadPhaseColor("rw", 102..118)
    object GreenBlue : QuadPhaseColor("gb", 119..135)
    object GreenWhite : QuadPhaseColor("gw", 136..152)
    object BlueWhite : QuadPhaseColor("bw", 153..169)
    object RedGreenBlue : QuadPhaseColor("rgb", 170..186)
    object RedGreenWhite : QuadPhaseColor("rgw", 187..203)
    object RedBlueWhite : QuadPhaseColor("rbw", 204..220)
    object GreenBlueWhite : QuadPhaseColor("gbw", 221..237)
    object RedGreenBlueWhite : QuadPhaseColor("rgbw", 238..255)

    companion object {
        val colors = listOf(
            Off, Red, Green, Blue, White, RedGreen, RedBlue, RedWhite, GreenBlue, GreenWhite, BlueWhite, RedGreenBlue,
            RedGreenWhite, RedBlueWhite, GreenBlueWhite, RedGreenBlueWhite
        ).map { it.id to it }.toMap()
    }

    class Config(override val colors: Map<String, QuadPhaseColor> = emptyMap()) : Color.Config()
}