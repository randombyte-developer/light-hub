package de.randombyte.lighthub.qlc

import org.redundent.kotlin.xml.Node

data class WindowState(
    val visible: Boolean = true,
    val x: Int = 0,
    val y: Int = 0,
    val width: Int = 50,
    val height: Int = 50
) {
    companion object {
        const val SLIDER_WIDTH = 60
        const val SLIDER_HEIGHT = 200

        val BUTTON = WindowState()
        val SLIDER = WindowState(width = 60, height = 200)
    }

    fun toXml(node: Node) {
        node.apply {
            "WindowState"(
                "Visible" to visible.toCapitalizedString,
                "X" to x,
                "Y" to y,
                "Width" to width,
                "Height" to height
            )
        }
    }

    private val Boolean.toCapitalizedString
        get() = if (this) "True" else "False"
}