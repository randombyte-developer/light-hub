package de.randombyte.lighthub.midi.akai

import de.randombyte.lighthub.midi.MidiHandler
import javax.sound.midi.*

/**
 * Specifically the Akai MPD26.
 */
class Akai(inDevice: MidiDevice, outDevice: MidiDevice) : MidiHandler(inDevice, outDevice) {
    companion object {
        const val NAME = "MPD26"

        fun findBestMatch(): Akai? = try {
            val devices = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

            val inDevice = devices.firstOrNull { "MidiInDevice" in it.javaClass.simpleName && NAME in it.deviceInfo.name }
            val outDevice = devices.firstOrNull { "MidiOutDevice" in it.javaClass.simpleName && NAME in it.deviceInfo.name }

            if (inDevice == null || outDevice == null) throw RuntimeException("Akai unavailable!")

            Akai(inDevice, outDevice)
        } catch (ex: MidiUnavailableException) {
            null
        }
    }

    override fun open(): Boolean {
        if (!super.open()) return false
        enableSpecialMode()
        return true
    }

    /**
     * This listener will only react to the special SysEx messages enabled with [enableSpecialMode].
     */
    fun setListener(listener: (SysEx.Signal) -> Unit) {
        inDevice.transmitter.receiver = object : Receiver {
            override fun send(message: MidiMessage, timestamp: Long) {
                listener(SysEx.parseSysEx(message.message) ?: return)
            }

            override fun close() {
                println("Closing")
            }
        }
    }

    /**
     * Sends a special SysEx message to the device to make it send every button press, even those that aren't mapped to
     * any midi signal. This is the mode the official editor operates in.
     */
    private fun enableSpecialMode() {
        sendSysEx(SysEx.SYSEX_SPECIAL_MODE)
    }
}