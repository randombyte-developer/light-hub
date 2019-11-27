package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.osc.devices.features.FixedColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.DimmableComponentsColor
import de.randombyte.lighthub.show.ColorSelector
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.utils.getElementWrappedAround
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ColorChangerFlow : Flow<ColorFeature>(acceptedDevices = lights as List<ColorFeature>) {

    var ticksTransitionDuration = 40

    private var dimmableColorGoals = mutableMapOf<DimmableComponentsColorFeature, DimmableComponentsColor>()

    override fun onActivate(device: ColorFeature) {
        // instantly change color, no transition
        device.setColor(getSelectedColor(device))
        (device as? ShutterFeature)?.fullIntensity()

        // delete all goals to prevent the instant change from being overwritten with old goals
        dimmableColorGoals.clear()
    }

    private fun changeColor(device: ColorFeature) {
        (device as? ShutterFeature)?.fullIntensity()

        when (device) {
            is DimmableComponentsColorFeature -> {
                dimmableColorGoals[device] = getSelectedColor(device) as DimmableComponentsColor
            }
            is FixedColorFeature -> {
                device.setColor(getSelectedColor(device))
            }
        }
    }

    override fun onTick(tick: ULong) {
        usedDevices.forEach { device ->
            if (isOnChange<ColorAutoPatternsConfig>(tick, device as Device)) {
                ColorSelector.selectNextColor(device)
                changeColor(device)
            }
        }

        dimmableColorGoals.forEach { (device, targetColor) ->
            val ticksUntilColorChanged = getTicksUntilNextChange<ColorAutoPatternsConfig>(tick, device as Device)
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
    }

    private fun getSelectedColor(device: ColorFeature): Color {
        val colorSetId = ColorSelector.selectedColorSetId
        val colorIndex = ColorSelector.colorSetSelectedColorIndex.getValue(device as Device)
        val colorId = device.colorSets.all.getValue(colorSetId).getElementWrappedAround(colorIndex)
        return device.colors.getValue(colorId)
    }

    // todo: better name, that's by no means linear
    private fun almostLinearTransform(current: Int, goal: Int, ticksLeft: Int) =
        current + ((goal - current) / ticksLeft.toDouble()).roundToInt()
}