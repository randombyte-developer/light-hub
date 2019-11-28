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

    var device: ColorFeature? = null

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
        device = devices.random()
        devices.forEach { (it as ShutterFeature).noLight() }
        (device as ShutterFeature).fullIntensity()
    }

    private fun setColor() {
        if (device == null) randomDevice()
        device!!.setColor(ColorSelector.getSelectedColor(device!!))
    }
}