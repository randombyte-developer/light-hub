package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.utils.multipleOf
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ColorChangerFlow(devices: List<ColorFeature>) : Flow<ColorFeature>(devices) {

    var ticksSinceLastBeat = 0

    private var dimmableColorGoals = mutableMapOf<DimmableComponentsColorFeature, String>()

    override fun onResume() {
        usedDevices.forEach { device ->
            (device as? MasterDimmerFeature)?.fullIntensity()
            (device as? StrobeFeature)?.noStrobe()
        }
    }

    override fun onBeat(beat: ULong) {
        ticksSinceLastBeat = 0

        usedDevices.forEach { device ->
            if (beat.multipleOf(device.colorAutoPatterns.changeBeatPhase)) {
                when (device) {
                    is DimmableComponentsColorFeature -> {
                        dimmableColorGoals[device] = device.colorCategories.warm.random()
                    }
                    is FixedColorFeature -> {
                        device.setColor(device.colors.getValue(device.colorCategories.warm.random()))
                    }
                }
            }

            if (device is RotationFeature) {
                if (beat.multipleOf(device.rotationAutoPatterns.changeBeatPhase)) {
                    device.rotationSpeed = device.rotationSpeeds.normal.random()
                }
            }
        }
    }

    override fun onTick(tick: ULong) {
        dimmableColorGoals.forEach { (device, targetColorId) ->
            if (device !in usedDevices) return@forEach

            val ticksUntilColorChanged = device.colorAutoPatterns.changeTicksDuration - ticksSinceLastBeat
            if (ticksUntilColorChanged <= 0) {
                // the color has changed and the transition is already done
                return@forEach
            }

            val targetColor = device.colors.getValue(targetColorId)
            val intermediateColor = device.getColor().transformComponents(targetColor) { currentComponentValue, targetComponentValue ->
                almostLinearTransform(
                    currentComponentValue,
                    targetComponentValue,
                    ticksLeft = ticksUntilColorChanged
                )
            }
            device.setColor(intermediateColor)
        }

        ticksSinceLastBeat++
    }

    // todo: better name, that's by no means linear
    private fun almostLinearTransform(current: Int, goal: Int, ticksLeft: Int) =
        current + ((goal - current) / ticksLeft.toDouble()).roundToInt()
}

