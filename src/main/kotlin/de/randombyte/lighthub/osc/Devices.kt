package de.randombyte.lighthub.osc

import de.randombyte.lighthub.osc.devices.HexClone
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.osc.devices.features.RgbFeature
import de.randombyte.lighthub.utils.intersects

object Devices {
    fun createDevicesFromConfig(): List<Device> {
        val (ledBar1, ledBar2) = createDeviceFromConfig(2, LedBar.Companion)
        val (tsssPar1, tsssPar2) = createDeviceFromConfig(2, TsssPar.Companion)
        val (hexPar1, hexPar2) = createDeviceFromConfig(2, HexPar.Companion)
        val (hexClone1, hexClone2) = createDeviceFromConfig(2, HexClone.Companion)

        checkChannels(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)
        checkCollisions(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)
        checkStrobeColor(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)
        checkColorCategories(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)

        return listOf(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2, hexClone1, hexClone2)
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

    private fun checkChannels(vararg devices: Device) {
        devices.forEach { device ->
            require(device.type.channelsCount == device.oscChannelList.allNestedChannels.size) {
                "[${device.type.id}] ${device.type.channelsCount} channels are defined, but " +
                        "actually ${device.oscChannelList.allNestedChannels.size} are implemented!"
            }
        }
    }

    private fun checkCollisions(vararg devices: Device) {
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

    private fun checkStrobeColor(vararg devices: RgbFeature) {
        devices.forEach { device ->
            require(device.colorCategories.strobe.isNotBlank()) {
                "[${device.type.id}] Strobe color is not set in the color categories config!"
            }
        }
    }

    private fun checkColorCategories(vararg devices: RgbFeature) {
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
}