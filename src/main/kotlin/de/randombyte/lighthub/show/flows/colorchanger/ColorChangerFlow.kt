package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
import de.randombyte.lighthub.show.flows.Flow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ColorChangerFlow(devices: List<ColorFeature>) : Flow<ColorFeature>(devices) {

    var tempo = 50 // todo: config
    var ticksUntilColorChange = 1
    private var colorGoals: Map<ColorFeature, String> = mapOf()

    override fun onResume() {
        forceColorChangeOnThisTick()
        usedDevices.forEach { device ->
            (device as? MasterDimmerFeature)?.fullIntensity()
            (device as? StrobeFeature)?.noStrobe()
        }
    }

    override fun onTick() {
        colorGoals.forEach { (device, targetColorId) ->
            if (device !in usedDevices) return@forEach
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
            ticksUntilColorChange = tempo
        }
    }

    private fun almostLinearTransform(current: Int, goal: Int, ticksUntilColorChange: Int) =
        current + ((goal - current) / ticksUntilColorChange.toDouble()).roundToInt()

    private fun createNewColorGoals() = usedDevices.map { device ->
         device to device.colorCategories.warm.random()
    }.toMap()

    fun forceColorChangeOnThisTick() {
        ticksUntilColorChange = 1
    }
}

