package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.*
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.flatten
import de.randombyte.lighthub.utils.intersects
import kotlin.reflect.KMutableProperty0
import kotlin.time.ExperimentalTime

@ExperimentalTime
object DevicesManager {
    lateinit var ledBar1: LedBar
    lateinit var ledBar2: LedBar

    lateinit var tsssPar1: TsssPar
    lateinit var tsssPar2: TsssPar

    lateinit var hexPar1: HexPar
    lateinit var hexPar2: HexPar

    lateinit var hexClone1: HexClone
    lateinit var hexClone2: HexClone

    lateinit var quadPhase1: QuadPhase
    lateinit var quadPhase2: QuadPhase

    lateinit var scanner1: Scanner
    lateinit var scanner2: Scanner
    lateinit var scanner3: Scanner
    lateinit var scanner4: Scanner

    val ledBars get() = listOf(ledBar1, ledBar2)
    val tsssPars get() = listOf(tsssPar1, tsssPar2)
    val hexPars get() = listOf(hexPar1, hexPar2)
    val hexClones get() = listOf(hexClone1, hexClone2)
    val quadPhases get() = listOf(quadPhase1, quadPhase2)
    val scanners get() = listOf(scanner1, scanner2, scanner3, scanner4)

    val pars get() = flatten<DimmableComponentsColorFeature>(tsssPars, hexPars, hexClones)
    val lights get() = flatten<Device>(ledBars, tsssPars, hexPars, hexClones, quadPhases, scanners)

    fun init() {
        createDeviceFromConfig(LedBar, ::ledBar1, ::ledBar2)
        createDeviceFromConfig(TsssPar, ::tsssPar1, ::tsssPar2)
        createDeviceFromConfig(HexPar, ::hexPar1, ::hexPar2)
        createDeviceFromConfig(HexClone, ::hexClone1, ::hexClone2)
        createDeviceFromConfig(QuadPhase, ::quadPhase1, ::quadPhase2)
        createDeviceFromConfig(Scanner, ::scanner1, ::scanner2, ::scanner3, ::scanner4)

        checkDevicesConfigurations()
    }

    private fun <T : Device> createDeviceFromConfig(type: Device.Type<T>, vararg fields: KMutableProperty0<T>) {
        val addresses = type.metaConfig.config.addresses
        if (addresses.size != fields.size) {
            throw RuntimeException("Exactly ${fields.size} addresses are needed for ${type.id}! ${addresses.size} addresses are set.")
        }

        addresses.forEachIndexed { index, address ->
            fields[index].set(type.constructor(index, address))
        }
    }

    private fun checkDevicesConfigurations() {
        val dimmableLights = listOf(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)
        val quadPhases = listOf(quadPhase1, quadPhase2)
        val scanners = listOf(scanner1, scanner2, scanner3, scanner4)
        val devices = (dimmableLights + quadPhases + scanners) as List<Device>

        checkChannels(devices)
        checkCollisions(devices)
        checkStrobeColor(devices as List<ColorFeature>) // should be before checking color-sets
        checkColorSets(devices)
        checkColorBounds(dimmableLights as List<DimmableComponentsColorFeature>)
        checkPanTiltBounds(scanners)
    }

    private fun checkChannels(devices: List<Device>) {
        devices.forEach { device ->
            require(device.type.channelsCount == device.oscChannelList.allNestedChannels.size) {
                "[${device.type.id}] ${device.type.channelsCount} channels are defined, but " +
                        "actually ${device.oscChannelList.allNestedChannels.size} are implemented!"
            }
        }
    }

    private fun checkCollisions(devices: List<Device>) {
        devices.forEachIndexed { indexOuter, devicePositionOuter ->
            devices.forEachIndexed innerLoop@{ indexInner, devicePositionInner ->
                if (indexOuter == indexInner) return@innerLoop // same device, continue

                require(!devicePositionOuter.addressRange.intersects(devicePositionInner.addressRange)) {
                    "DMX channels collide: " +
                            "${devicePositionOuter.type.id}(channels=${devicePositionOuter.type.channelsCount}) " +
                            "starting at ${devicePositionOuter.dmxAddress} " +
                            "collides with " +
                            "${devicePositionInner.type.id}(channels=${devicePositionInner.type.channelsCount}) " +
                            "starting at ${devicePositionInner.dmxAddress}"
                }
            }
        }
    }

    private fun checkStrobeColor(devices: List<ColorFeature>) {
        devices.forEach { device ->
            require(device.colorSets.strobe.isNotBlank()) {
                "[${device.type.id}] Strobe color is not set in the color-sets config!"
            }
        }
    }

    private fun checkColorSets(devices: List<ColorFeature>) {
        devices.forEach { device ->
            device.colorSets.all.forEach { (setId, colorsIds) ->
                colorsIds.forEach { colorId ->
                    require(colorId in device.colors.keys) {
                        "[${device.type.id}] Color '$colorId' is defined in the color-set '$setId' " +
                                "but is missing in the color definitions of the device!"
                    }
                }
            }
        }
    }

    private fun checkColorBounds(devices: List<DimmableComponentsColorFeature>) {
        devices.forEach { device ->
            device.colors.forEach { (colorId, color) ->
                color.components.forEach { component ->
                    require(component.get() in Ranges.DMX_RANGE) {
                        "[${device.type.id}] At least one component of the color '$colorId' is not in 0..255!"
                    }
                }
            }
        }
    }

    private fun checkPanTiltBounds(devices: List<Scanner>) {
        devices.forEach { device ->
            with (device.panTiltBounds) {
                require(`pan-min` <= `pan-max`) { "[${device.type.id}] pan-min must be smaller than or equal to pan-max!" }
                require(`tilt-min` <= `tilt-max`) { "[${device.type.id}] tilt-min must be smaller than or equal to tilt-max!" }
            }
        }
    }
}