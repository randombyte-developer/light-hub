package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.ColorSelector
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.events.UpdateColor
import kotlin.time.ExperimentalTime

@ExperimentalTime
object OneLightOnly : MasterFlow<ColorFeature>(isFallback = true, devices = lights as List<ColorFeature>) {

    override val configFolderName = "one-light-only"

    const val LIGHT_COUNT = 2
    val selectedDevices = mutableListOf<ColorFeature>()

    init {
        subscribeIfActive<UpdateColor> {
            setColor()
        }
    }

    override fun onActivate() {
        super.onActivate()

        randomDevice()
        setColor()
    }

    private fun randomDevice() {
        selectedDevices.clear()
        repeat(LIGHT_COUNT) { selectedDevices += devices.filterNot { it in selectedDevices }.random()}
        devices.forEach { (it as ShutterFeature).noLight() }
        selectedDevices.forEach { (it as ShutterFeature).fullIntensity() }
    }

    private fun setColor() {
        if (selectedDevices.isEmpty()) randomDevice()
        selectedDevices.forEach { device ->
            device.setColor(ColorSelector.getSelectedColor(device))
        }
    }
}