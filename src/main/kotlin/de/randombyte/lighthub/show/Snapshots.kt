package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.Device

class ShowSnapshot(devices: List<Device>) {
    val deviceSnapshots: List<DeviceSnapshot> = devices.map { DeviceSnapshot(it) }

    fun restore() = deviceSnapshots.forEach { it.restore() }
}

class DeviceSnapshot(val device: Device) {
    val channelValues = device.oscChannelList.channels.mapIndexed { index, channel ->
        index to channel.lastValue
    }

    fun restore() {
        channelValues.forEach { (listIndex, value) ->
            device.oscChannelList.channels[listIndex].sendValue(value)
        }
    }
}