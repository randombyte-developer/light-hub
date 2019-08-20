package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.Device

class SnapshotManager(val devices: List<Device>) {

    private var hasSnapshot: Boolean = false
    private var snapshot: MutableMap<Device, DeviceSnapshot?> = devices.associateWith { null }.toMutableMap()

    private class DeviceSnapshot(device: Device) {
        val channels = device.oscChannelList.channels.map {
            it.relativeDmxAddress to it.lastValue
        }
    }

    /**
     * @return false if there is already a snapshot, true if saving the snapshot was successful
     */
    fun saveSnapshot(): Boolean {
        if (hasSnapshot) return false
        snapshot.forEach { (device, _) ->
            snapshot[device] = DeviceSnapshot(device)
        }
        return true
    }

    fun restoreSnapshot() {
        if (!hasSnapshot) return
        snapshot.forEach { (device, deviceSnapshot) ->
            deviceSnapshot!!.channels.forEach { (address, value) ->
                device.oscChannelList.getByRelativeDmxAddress(address)!!.sendValue(value)
            }
            snapshot[device] = null
        }
    }
}