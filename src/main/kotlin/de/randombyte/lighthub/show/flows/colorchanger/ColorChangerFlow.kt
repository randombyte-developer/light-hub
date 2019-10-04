package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.colors.DimmableComponentsColor
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.utils.multipleOf
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ColorChangerFlow(devices: List<ColorFeature>) : Flow<ColorFeature>(devices) {

    companion object {
      const val NONE_COLOR_ID = "none" // basically shutter closed, no light
    }

    var colorSetSelector: ColorSetsConfig.() -> List<String> = { `set-1` }
    var ticksTransitionDuration = 10

    private var ticksSinceLastBeat = 0

    private var dimmableColorGoals = mutableMapOf<DimmableComponentsColorFeature, DimmableComponentsColor>()

    override fun onResume() {
        usedDevices.forEach { device ->
            (device as? StrobeFeature)?.noStrobe()

            // directly set a color when resuming
            val colorId = colorSetSelector(device.colorSets).random()
            if (colorId == NONE_COLOR_ID) {
                (device as? ShutterFeature)?.noLight()
                return@forEach
            }

            (device as? ShutterFeature)?.fullIntensity()

            // instantly change color, no transition
            device.setColor(device.colors.getValue(colorId))

            // delete all goals to prevent the instant change from being overwritten with old goals
            dimmableColorGoals.clear()
        }
    }

    override fun onBeat(beat: ULong) {
        ticksSinceLastBeat = 0

        usedDevices.forEach { device ->
            with(device.colorAutoPatterns) {
                val specificDeviceOffset = `change-beats-offset` * (device as Device).number
                if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                    changeColor(device)
                }
            }
        }
    }

    private fun changeColor(device: ColorFeature) {
        val colorId = colorSetSelector(device.colorSets).random()
        if (colorId == NONE_COLOR_ID) {
            (device as? ShutterFeature)?.noLight()
            return
        }

        (device as? ShutterFeature)?.fullIntensity()

        when (device) {
            is DimmableComponentsColorFeature -> {
                dimmableColorGoals[device] = device.colors.getValue(colorId)
            }
            is FixedColorFeature -> {
                device.setColor(device.colors.getValue(colorId))
            }
        }
    }

    override fun onTick(tick: ULong) {
        dimmableColorGoals.forEach { (device, targetColor) ->
            if (device !in usedDevices) return@forEach

            val ticksUntilColorChanged = ticksTransitionDuration - ticksSinceLastBeat
            if (ticksUntilColorChanged <= 0) {
                // the color has changed and the transition is already done
                return@forEach
            }

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