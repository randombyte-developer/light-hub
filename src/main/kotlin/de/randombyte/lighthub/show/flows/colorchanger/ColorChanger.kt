package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.show.ThatShow
import de.randombyte.lighthub.show.flows.Flow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ColorChanger(show: ThatShow) : Flow(show) {

    private var ticksUntilColorChange = 0
    private var colorGoals: Map<ColorFeature, String> = mapOf()

    override fun onTick() {
        colorGoals.forEach { (device, targetColorId) ->
            if (device !is DimmableComponentsColorFeature) return // todo: support other devices

            val targetColor = device.colors.getValue(targetColorId)
            val intermediateColor = device.getColor().transformComponents(targetColor) { current, other ->
                almostLinearTransform(current, other, ticksUntilColorChange)
            }
            device.setColor(intermediateColor)
        }

        ticksUntilColorChange--
        if (ticksUntilColorChange <= 0) {
            colorGoals = createNewColorGoals()
            ticksUntilColorChange = 50 // Akai.getTempoFader
        }
    }

    private fun almostLinearTransform(current: Int, goal: Int, ticksUntilColorChange: Int) =
        current + ((goal - current) / ticksUntilColorChange.toDouble()).roundToInt()

    private fun createNewColorGoals() = show.colorLights.map { device ->
         device to device.colorCategories.warm.random()
    }.toMap()
}

