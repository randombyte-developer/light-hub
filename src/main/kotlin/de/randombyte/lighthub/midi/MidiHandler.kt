package de.randombyte.lighthub.midi

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.SysexMessage

open class MidiHandler(val inDevice: MidiDevice, val outDevice: MidiDevice) {
    open fun open(): Boolean {
        try {
            if (!inDevice.isOpen) inDevice.open()
            if (!outDevice.isOpen) outDevice.open()
        } catch (ex: MidiUnavailableException) {
            return false
        }

        return true
    }

    protected fun sendSysEx(data: UByteArray) {
        outDevice.receiver.send(SysexMessage(data.toByteArray(), data.size), -1)
    }
}