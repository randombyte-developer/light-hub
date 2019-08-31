package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.Device

class SnapshotManager(val devices: List<Device>) {

    var hasSnapshot: Boolean = false
        private set

    private var snapshot: MutableMap<Device, DeviceSnapshot?> = devices.associateWith { null }.toMutableMap()

    private class DeviceSnapshot(device: Device) {
        val channels = device.oscChannelList.channels.map {
            it.relativeDmxAddress to it.lastValue
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
            deviceSnapshot!!.channels.forEach { (address, value) ->
                device.oscChannelList.getByRelativeDmxAddress(address)!!.sendValue(value)
            }
            snapshot[device] = null
        }
        hasSnapshot = false
    }
}