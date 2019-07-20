package de.randombyte.lighthub.qlc

import org.redundent.kotlin.xml.Node

class WidgetAppearance(
    val frameStyle: FrameStyle = FrameStyle.None,
    val foregroundColor: String = "Default",
    val backgroundColor: String = "Default",
    val backgroundImage: String = "None",
    val font: String = "None"
) {
    enum class FrameStyle(val id: String) {
        None("None"), Sunken("Sunken");
    }

    companion object {
        val DEFAULT = WidgetAppearance(frameStyle = FrameStyle.None)
        val SUNKEN = WidgetAppearance(frameStyle = FrameStyle.Sunken)
    }

    fun toXml(node: Node) {
        node.apply {
            "Appearance" {
                "FrameStyle" { -"None" }
                "ForegroundColor" { -"Default" }
                "BackgroundColor" { -"Default" }
                "BackgroundImage" { -"None" }
                "Font" { -"Default" }
            }
        }
    }
}