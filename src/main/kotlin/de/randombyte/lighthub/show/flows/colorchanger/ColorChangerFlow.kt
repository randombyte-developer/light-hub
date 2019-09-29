package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.show.flows.Flow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ColorChangerFlow(devices: List<ColorFeature>) : Flow<ColorFeature>(devices) {

    var tempo = 50 // todo: config
    var ticksUntilNewGoals = 1

    private var colorGoals: Map<ColorFeature, String> = mapOf()
    private var rotationGoals: Map<RotationFeature, Int> = mapOf()

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

            when (device) {
                is DimmableComponentsColorFeature -> {
                    val targetColor = device.colors.getValue(targetColorId)
                    val intermediateColor = device.getColor().transformComponents(targetColor) { current, other ->
                        almostLinearTransform(current, other, ticksUntilNewGoals)
                    }
                    device.setColor(intermediateColor)
                }
                is FixedColorFeature -> {
                    device.setColor(device.colors.getValue(targetColorId))
                }
            }
        }

        rotationGoals.forEach { (device, targetRotation) ->
            device.rotationSpeed = targetRotation
        }

        ticksUntilNewGoals--
        if (ticksUntilNewGoals <= 0) {
            colorGoals = createNewColorGoals()
            rotationGoals = createNewRotationGoals()
            ticksUntilNewGoals = tempo
        }
    }

    private fun almostLinearTransform(current: Int, goal: Int, ticksUntilColorChange: Int) =
        current + ((goal - current) / ticksUntilColorChange.toDouble()).roundToInt()

    private fun createNewColorGoals() = usedDevices.map { device ->
         device to device.colorCategories.warm.random() // todo: warm/cold
    }.toMap()

    private fun createNewRotationGoals() = usedDevices.filterIsInstance<RotationFeature>().map { device ->
        device to device.rotationSpeeds.normal.random()
    }.toMap()

    fun forceColorChangeOnThisTick() {
        ticksUntilNewGoals = 1
    }
}

