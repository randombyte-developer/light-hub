package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.Device

class SnapshotManager(val devices: List<Device>) {

    var hasSnapshot: Boolean = false
        private set

    private var snapshot: MutableMap<Device, DeviceSnapshot?> = devices.associateWith { null }.toMutableMap()

    private class DeviceSnapshot(device: Device) {
        val channelValues = device.oscChannelList.channels.mapIndexed { index, channel ->
            index to channel.lastValue
        }
    }

    fun saveSnapshot() {
        if (hasSnapshot) return
        snapshot.forEach { (device, _) ->
            snapshot[device] = DeviceSnapshot(device)
        }
        hasSnapshot = true
    }

    fun restoreSnapshot() {
        if (!hasSnapshot) return
        snapshot.forEach { (device, deviceSnapshot) ->
            deviceSnapshot!!.channelValues.forEach { (listIndex, value) ->
                device.oscChannelList.channels[listIndex].sendValue(value)
            }
            snapshot[device] = null
        }
        hasSnapshot = false
    }
}