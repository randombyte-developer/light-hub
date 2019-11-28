package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.osc.devices.features.FixedColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.osc.devices.features.colors.DimmableComponentsColor
import de.randombyte.lighthub.osc.devices.features.colors.FixedColorOff
import de.randombyte.lighthub.show.ColorSelector
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.events.UpdateColor
import de.randombyte.lighthub.show.flows.Flow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ColorChangerFlow : Flow<ColorFeature>(acceptedDevices = lights as List<ColorFeature>) {

    var ticksTransitionDuration = 20

    private var dimmableColorGoals = mutableMapOf<DimmableComponentsColorFeature, DimmableComponentsColor>()

    init {
        subscribe<UpdateColor> {
            onActivate()
        }
    }

    override fun onActivate(device: ColorFeature) {
        // instantly change color, no transition
        if (device is FixedColorFeature) {
            changeFixedColor(device)
        } else {
            (device as? ShutterFeature)?.fullIntensity()
            device.setColor(ColorSelector.getSelectedColor(device))
        }

        // delete all goals to prevent the instant change from being overwritten with old goals

        dimmableColorGoals.clear()
    }

    override fun onTick(tick: ULong) {
        dimmableColorGoals.forEach { (device, targetColor) ->
            if (device !in usedDevices) return@forEach

            val ticksSinceLastChange = getTicksSinceLastChange<ColorAutoPatternsConfig>(tick, device as Device)
            val ticksUntilColorTransitionComplete = ticksTransitionDuration - ticksSinceLastChange
            if (ticksUntilColorTransitionComplete <= 0) {
                // the color has changed and the transition is already done
                return@forEach
            }

            val intermediateColor = device.getColor().transformComponents(targetColor) { currentComponentValue, targetComponentValue ->
                almostLinearTransform(
                    currentComponentValue,
                    targetComponentValue,
                    ticksLeft = ticksUntilColorTransitionComplete
                )
            }
            device.setColor(intermediateColor)
        }

        usedDevices.forEach { device ->
            if (isOnChange<ColorAutoPatternsConfig>(tick, device as Device)) {
                ColorSelector.selectNextColor(device)
                changeColor(device)
            }
        }
    }

    private fun changeColor(device: ColorFeature) {
        when (device) {
            is DimmableComponentsColorFeature -> {
                dimmableColorGoals[device] = ColorSelector.getSelectedColor(device) as DimmableComponentsColor
            }
            is FixedColorFeature -> {
                changeFixedColor(device)
            }
        }
    }

    private fun changeFixedColor(device: FixedColorFeature) {
        val color = ColorSelector.getSelectedColor(device)
        device.setColor(color)
        if (color !is FixedColorOff) {
            (device as? ShutterFeature)?.fullIntensity()
        }
    }

    // todo: better name, that's by no means linear
    private fun almostLinearTransform(current: Int, goal: Int, ticksLeft: Int) =
        current + ((goal - current) / ticksLeft.toDouble()).roundToInt()
}