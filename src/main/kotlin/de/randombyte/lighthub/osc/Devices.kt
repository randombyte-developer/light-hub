package de.randombyte.lighthub.osc

import de.randombyte.lighthub.osc.devices.*
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.intersects

object Devices {
    fun createDevicesFromConfig(): List<Device> {
        val (ledBar1, ledBar2) = createDeviceFromConfig(2, LedBar)
        val (tsssPar1, tsssPar2) = createDeviceFromConfig(2, TsssPar)
        val (hexPar1, hexPar2) = createDeviceFromConfig(2, HexPar)
        val (hexClone1, hexClone2) = createDeviceFromConfig(2, HexClone)
        val (quadPhase1, quadPhase2) = createDeviceFromConfig(2, QuadPhase)
        val (scanner1, scanner2, scanner3, scanner4) = createDeviceFromConfig(4, Scanner)

        val dimmableLights = listOf(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)
        val quadPhases = listOf(quadPhase1, quadPhase2)
        val scanners = listOf(scanner1, scanner2, scanner3, scanner4)
        val devices = (dimmableLights + quadPhases + scanners) as List<Device>

        checkChannels(devices)
        checkCollisions(devices)
        checkStrobeColor(devices as List<ColorFeature>) // should be before checking color categories
        checkColorCategories(devices)
        checkColorBounds(dimmableLights as List<DimmableComponentsColorFeature>)
        checkPanTiltBounds(scanners)

        return listOf(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2,
            quadPhase1, quadPhase2, scanner1, scanner2, scanner3, scanner4)
    }

    private fun <T : Device> createDeviceFromConfig(amount: Int, type: Device.Type<T>): List<T> {
        type.metaConfig.reload()
        type.reloadConfigs()

        val addresses = type.metaConfig.config.addresses
        if (addresses.size != amount) {
            throw RuntimeException("Exactly $amount addresses are needed for ${type.id}! ${addresses.size} addresses are set.")
        }

        val devices = addresses.mapIndexed { index, address -> type.constructor(index, address) }
        return devices
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
            require(device.colorCategories.strobe.isNotBlank()) {
                "[${device.type.id}] Strobe color is not set in the color categories config!"
            }
        }
    }

    private fun checkColorCategories(devices: List<ColorFeature>) {
        devices.forEach { device ->
            device.colorCategories.all.forEach { (categoryId, colorsIds) ->
                colorsIds.forEach { colorId ->
                    require(colorId in device.colors.keys) {
                        "[${device.type.id}] Color '$colorId' is defined in the color category '$categoryId' " +
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
            with (device.panTiltAutoPatterns) {
                require(`pan-min` <= `pan-max`) { "[${device.type.id}] pan-min must be smaller than or equal to pan-max!" }
                require(`tilt-min` <= `tilt-max`) { "[${device.type.id}] tilt-min must be smaller than or equal to tilt-max!" }
            }
        }
    }
}