package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.utils.multipleOf
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ColorChangerFlow(devices: List<ColorFeature>) : Flow<ColorFeature>(devices) {

    var ticksTransitionDuration = 10

    private var ticksSinceLastBeat = 0

    private var dimmableColorGoals = mutableMapOf<DimmableComponentsColorFeature, String>()

    override fun onResume() {
        usedDevices.forEach { device ->
            (device as? ShutterFeature)?.fullIntensity()
            (device as? StrobeFeature)?.noStrobe()

            // instantly change color, no transition
            device.setColor(device.colors.getValue(device.colorCategories.warm.random()))
            // delete all goals to prevent the instant change from being overwritten with old goals
            dimmableColorGoals.clear()
            if (device is RotationFeature) changeRotation(device)
            if (device is PanTiltFeature) changePanTilt(device)
        }
    }

    private fun changeColor(device: ColorFeature) {
        when (device) {
            is DimmableComponentsColorFeature -> {
                dimmableColorGoals[device] = device.colorCategories.warm.random()
            }
            is FixedColorFeature -> {
                device.setColor(device.colors.getValue(device.colorCategories.warm.random()))
            }
        }
    }

    private fun changeRotation(device: RotationFeature) {
        device.rotationSpeed = device.rotationSpeeds.normal.random()
    }

    private fun changePanTilt(device: PanTiltFeature) {
        val position = with (device.panTiltAutoPatterns) {
            PanTiltFeature.Position(
                pan = Random.nextInt(from = `pan-min`, until = `pan-max` + 1),
                tilt = Random.nextInt(from = `tilt-min`, until = `tilt-max` + 1)
            )
        }
        device.position = position
    }

    override fun onBeat(beat: ULong) {
        ticksSinceLastBeat = 0

        usedDevices.forEach { device ->
            val rawDevice = device as Device

            with(device.colorAutoPatterns) {
                val specificDeviceOffset = `change-beats-offset` * rawDevice.number
                if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                    changeColor(device)
                }
            }

            if (device is RotationFeature) {
                with(device.rotationAutoPatterns) {
                    val specificDeviceOffset = `change-beats-offset` * rawDevice.number
                    if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                        changeRotation(device)
                    }
                }
            }

            if (device is PanTiltFeature) {
                with(device.panTiltAutoPatterns) {
                    val specificDeviceOffset = `change-beats-offset` * rawDevice.number
                    if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                        changePanTilt(device)
                    }
                }
            }
        }
    }

    override fun onTick(tick: ULong) {
        dimmableColorGoals.forEach { (device, targetColorId) ->
            if (device !in usedDevices) return@forEach

            val ticksUntilColorChanged = ticksTransitionDuration - ticksSinceLastBeat
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

