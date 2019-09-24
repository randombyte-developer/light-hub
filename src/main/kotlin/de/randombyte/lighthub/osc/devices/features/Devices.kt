package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.utils.intersects

object Devices {

    class Collision(val deviceA: Device, val deviceB: Device) {
        override fun toString() =
            "${deviceA.type.id}(channels=${deviceA.channelsCount}) starting at ${deviceA.dmxAddress} " +
                    "collides with ${deviceB.type.id}(channels=${deviceB.channelsCount}) starting at ${deviceB.dmxAddress}"
    }

    /**
     * @return the first collision of two devices if there are any
     */
    fun checkCollision(devices: List<Device>): Collision? {
        devices.forEachIndexed { indexOuter, devicePositionOuter ->
            devices.forEachIndexed innerLoop@{ indexInner, devicePositionInner ->
                if (indexOuter == indexInner) return@innerLoop // continue

                if (devicePositionOuter.addressRange.intersects(devicePositionInner.addressRange)) {
                    return Collision(devicePositionOuter, devicePositionInner)
                }
            }
        }

        return null
    }
}